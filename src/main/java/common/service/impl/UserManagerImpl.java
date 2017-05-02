package common.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.Constants;
import common.dao.UserDao;
import common.exception.DBException;
import common.model.User;
import common.service.PasswordTokenManager;
import common.service.RoleManager;
import common.service.UserManager;
import common.service.UserService;
import common.service.mail.UserMail;
import common.webapp.converter.UserConverterFactory;
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
        return userDao.get(new Long(userId));
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
            user.setUsername(user.getUsername().toLowerCase());
        }

        boolean passwordChanged = false;

        if (passwordEncoder != null) {
            if (user.getVersion() == null) {
                passwordChanged = true;
            } else {
                String currentPassword = userDao.getPasswordById(user.getId());

                if (user.getPassword() == null) {
                    user.setPassword(currentPassword);
                    user.setConfirmPassword(currentPassword);
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
            return userDao.saveUser(user);
        } catch (Exception e) {
            user.setPassword(null);
            user.setConfirmPassword(null);

            if (user.getVersion() == null) {
                throw new DBException("errors.insert", e);
            } else {
                throw new DBException("errors.update", e);
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
        userDao.remove(new Long(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadUsers(UploadForm uploadForm) {
        List<User> userList = UserConverterFactory.createConverter(uploadForm.getFileType()).convert(uploadForm.getFileData());

        userList.forEach(user -> {
            // デフォルトの要再認証日時を設定する
            user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
            // 新規登録時は権限を一般で設定する
            user.getRoles().clear();
            user.addRole(roleManager.getRole(Constants.USER_ROLE));
            user.setConfirmPassword(user.getPassword());
            user.setEnabled(true);

            if (validator.validate(user).size() == 0) {
                saveUser(user);
                uploadForm.setCount(uploadForm.getCount() + 1);
            } else {
                // エラー有りの場合
                uploadForm.addErrorNo(uploadForm.getCount() + uploadForm.getErrorNo().size() + 1);
            }
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
        user.getRoles().clear();
        user.addRole(roleManager.getRole(Constants.USER_ROLE));
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
    public void activateRoles(User user) {
        user.setRoles(user.getRoles().stream().map(role -> roleManager.getRole(role.getName())).collect(Collectors.toSet()));
    }

    /**
     * {@inheritDoc}
     */
    @Autowired
    @Override
    public void setUserDao(UserDao userDao) {
        this.dao = userDao;
        this.paginatedDao = userDao;
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
