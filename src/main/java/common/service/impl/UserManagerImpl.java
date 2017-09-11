package common.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.Constants;
import common.dao.UserDao;
import common.exception.DatabaseException;
import common.model.Role;
import common.model.User;
import common.service.PasswordTokenManager;
import common.service.RoleManager;
import common.service.UserManager;
import common.service.UserService;
import common.service.mail.UserMail;
import common.webapp.converter.FileType;
import common.webapp.converter.UserFileConverterFactory;
import common.webapp.form.UploadForm;

/**
 * ユーザ処理の実装クラス.
 */
@Service("userManager")
@WebService(serviceName = "UserService", endpointInterface = "common.service.UserService")
public class UserManagerImpl extends PaginatedManagerImpl<User, Long> implements UserManager, UserService {

    /** ユーザDAO */
    private UserDao userDao;

    /** パスワードエンコーダー */
    @Autowired(required = false)
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /** パスワードトークン処理のクラス */
    @Autowired(required = false)
    private PasswordTokenManager passwordTokenManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

    /** Userメール処理クラス */
    @Autowired
    private UserMail userMail;

    /** 検証ツールクラス */
    @Autowired
    private Validator validator;

    /**
     * デフォルト・コンストラクタ.
     */
    public UserManagerImpl() {
    }

    /**
     * コンストラクタ.
     *
     * @param userDao
     *            ユーザDAO
     */
    @Autowired
    public UserManagerImpl(UserDao userDao) {
        super(userDao);
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers() {
        return userDao.getAllDistinct();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(String userId) {
        return userDao.get(Long.valueOf(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) {
        return (User) userDao.loadUserByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        if (user.getVersion() == null) {
            // 登録の場合
            user.setUsername(user.getUsername().toLowerCase());
        }

        boolean passwordChanged = false;

        if (passwordEncoder != null) {
            if (user.getVersion() == null) {
                // 登録の場合
                passwordChanged = true;
            } else {
                // 更新の場合
                String currentPassword = userDao.getPasswordById(user.getId());

                if (user.getPassword() == null) {
                    // パスワードが空の場合、パスワードは同じものを設定する
                    user.setPassword(currentPassword);
                    user.setConfirmPassword(user.getPassword());
                }
                if (currentPassword == null) {
                    passwordChanged = true;
                } else {
                    if (!currentPassword.equals(user.getPassword())) {
                        passwordChanged = true;
                    }
                }
            }

            if (passwordChanged) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setConfirmPassword(user.getPassword());
            }
        } else {
            log.warn("PasswordEncoder not set, skipping password encryption...");
        }

        try {
            user.setRoles(roleManager.getRoles(user.getRoles()));
            return userDao.saveUser(user);
        } catch (Exception e) {
            user.setPassword(null);
            user.setConfirmPassword(null);

            if (user.getVersion() == null) {
                throw new DatabaseException("errors.insert", e);
            } else {
                throw new DatabaseException("errors.update", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUser(User user) {
        userDao.remove(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUser(String userId) {
        userDao.remove(Long.valueOf(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadUsers(UploadForm uploadForm) {
        List<User> userList = UserFileConverterFactory.createConverter(FileType.of(uploadForm.getFileType())).convert(uploadForm.getFileData());

        userList.forEach(user -> {
            int rowNo = 2; // 1行目はヘッダー行のため、2から開始する
            // デフォルトの要再認証日時を設定する
            user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
            // 新規登録時は権限を一般で設定する
            user.addRole(new Role(Constants.USER_ROLE));
            user.setConfirmPassword(user.getPassword());
            user.setEnabled(true);

            Set<ConstraintViolation<User>> results = validator.validate(user);

            if (results.size() == 0) {
                saveUser(user);
                uploadForm.setCount(uploadForm.getCount() + 1);
            } else {
                // エラー有りの場合
                List<String> errorMessage = results.stream().sorted((o1, o2) -> {
                    int c = o1.getPropertyPath().toString().compareTo(o2.getPropertyPath().toString());
                    return c == 0 ? o1.getMessage().compareTo(o2.getMessage()) : c;
                })
                        .map(error -> error.getMessage().replaceAll("\\{0\\}", getText("user." + error.getPropertyPath().toString())))
                        .collect(Collectors.toList());

                uploadForm.addErrorMessage(rowNo, errorMessage);
            }

            rowNo++;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveSignupUser(User user) {
        // デフォルトの要再認証日時を設定する
        user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
        // 新規登録時は権限を一般で設定する
        user.addRole(new Role(Constants.USER_ROLE));
        user = saveUser(user);

        // 登録完了メールを送信する
        userMail.sendSignupEmail(user);

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User enableUser(User user) {
        user.setConfirmPassword(user.getPassword());
        user.setEnabled(true);

        return save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(String username, String token) {
        return isRecoveryTokenValid(getUserByUsername(username), token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(User user, String token) {
        return passwordTokenManager.isRecoveryTokenValid(user, token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recoveryPassword(String username) {
        userMail.sendPasswordRecoveryEmail(getUserByUsername(username));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updatePassword(String username, String currentPassword, String recoveryToken, String newPassword) {
        User user = getUserByUsername(username);

        if (isRecoveryTokenValid(user, recoveryToken)) {
            user.setPassword(newPassword);
            user.setEnabled(true);
            user = saveUser(user);

            userMail.sendUpdatePasswordEmail(user);

            return user;
        } else if (StringUtils.isNotBlank(currentPassword)) {
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(newPassword);
                user = saveUser(user);

                return user;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }
}
