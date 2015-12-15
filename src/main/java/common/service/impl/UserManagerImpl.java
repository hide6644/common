package common.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.Constants;
import common.dao.UserDao;
import common.exception.DBException;
import common.model.Role;
import common.model.User;
import common.service.MailEngine;
import common.service.PasswordTokenManager;
import common.service.RoleManager;
import common.service.UserManager;
import common.service.UserService;
import common.webapp.converter.UserConverterFactory;
import common.webapp.form.UploadForm;

/**
 * ユーザ処理の実装クラス.
 */
@Service("userManager")
@WebService(serviceName = "UserService", endpointInterface = "common.service.UserService")
public class UserManagerImpl extends GenericManagerImpl<User, Long> implements UserManager, UserService {

    /** ユーザDAO */
    private UserDao userDao;

    /** パスワードエンコーダー */
    @Autowired(required = false)
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /** メールを処理するクラス */
    @Autowired(required = false)
    private MailEngine mailEngine;

    /** Simple Mailメッセージ */
    @Autowired(required = false)
    private SimpleMailMessage mailMessage;

    /** パスワードトークン処理のクラス */
    @Autowired(required = false)
    private PasswordTokenManager passwordTokenManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

    /** 検証ツールクラス */
    @Autowired
    private Validator validator;

    /** メッセージソースアクセサー */
    private MessageSourceAccessor messages;

    /** ユーザ本登録メールのテンプレート */
    private String accountCreatedTemplate = "accountCreated.vm";

    /** パスワード回復案内メールのテンプレート */
    private String passwordRecoveryTemplate = "passwordRecovery.vm";

    /** パスワード更新メールのテンプレート */
    private String passwordUpdatedTemplate = "passwordUpdated.vm";

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
                String currentPassword = userDao.getUserPassword(user.getId());
                if (user.getPassword() == null) {
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
            return userDao.saveUser(user);
        } catch (Exception e) {
            user.setPassword(null);
            user.setConfirmPassword(user.getPassword());
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
    public List<User> search(String searchTerm) {
        return super.search(searchTerm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadUsers(UploadForm uploadForm) {
        @SuppressWarnings("unchecked")
        List<User> userList = (List<User>) UserConverterFactory.createConverter(uploadForm.getFileType()).convert(uploadForm.getFileData());

        for (User user : userList) {
            if (checkUploadUser(user)) {
                saveUser(user);
                uploadForm.setCount(uploadForm.getCount() + 1);
            } else {
                // エラー有りの場合
                uploadForm.addErrorNo(uploadForm.getCount() + uploadForm.getErrorNo().size() + 1);
            }
        }
    }

    /**
     * アップロードファイルのエラーチェックを行う.
     *
     * @param user
     *            ユーザ
     * @return true:エラーなし、false:エラーあり
     */
    private boolean checkUploadUser(User user) {
        // デフォルトの要再認証日時を設定する
        user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
        // 新規登録時は権限を一般で設定する
        user.getRoles().clear();
        user.addRole(roleManager.getRole(Constants.USER_ROLE));
        user.setConfirmPassword(user.getPassword());
        user.setEnabled(true);

        // エラーチェック
        return validator.validate(user).size() == 0;
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

        return saveUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User enableUser(User user) {
        user.setConfirmPassword(user.getPassword());
        user.setEnabled(true);

        return saveUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSignupUserEmail(User user, String urlTemplate) {
        String url = buildRecoveryPasswordUrl(user, urlTemplate);

        sendUserEmail(user, accountCreatedTemplate, messages.getMessage("signupForm.email.subject"), messages.getMessage("signupForm.email.message"), url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendCreatedUserEmail(User user, String urlTemplate) {
        String url = buildRecoveryPasswordUrl(user, urlTemplate);

        sendUserEmail(user, accountCreatedTemplate, messages.getMessage("userSaveForm.email.subject"), messages.getMessage("userSaveForm.email.message"), url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildRecoveryPasswordUrl(User user, String urlTemplate) {
        String token = generateRecoveryToken(user);
        String username = user.getUsername();

        return StringUtils.replaceEach(urlTemplate, new String[] { "{username}", "{token}" }, new String[] { username, token });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRecoveryToken(User user) {
        return passwordTokenManager.generateRecoveryToken(user);
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
    public void sendPasswordRecoveryEmail(String username, String urlTemplate) {
        User user = getUserByUsername(username);
        String url = buildRecoveryPasswordUrl(user, urlTemplate);

        sendUserEmail(user, passwordRecoveryTemplate, messages.getMessage("updatePasswordForm.email.subject"), messages.getMessage("updatePasswordForm.recovery.email.message"), url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updatePassword(String username, String currentPassword, String recoveryToken, String newPassword, String applicationUrl) {
        User user = getUserByUsername(username);

        if (isRecoveryTokenValid(user, recoveryToken)) {
            user.setPassword(newPassword);
            user.setEnabled(true);
            user = saveUser(user);

            sendUserEmail(user, passwordUpdatedTemplate, messages.getMessage("updatePasswordForm.email.subject"), messages.getMessage("updatePasswordForm.email.message"), applicationUrl);

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
        Set<Role> userRoles = new HashSet<Role>();

        for (Role role : user.getRoles()) {
            userRoles.add(roleManager.getRole(role.getName()));
        }

        user.setRoles(userRoles);
    }

    /**
     * メールを送信する.
     *
     * @param user
     *            ユーザ
     * @param template
     *            テンプレート
     * @param subject
     *            件名
     * @param message
     *            本文
     * @param url
     *            URL
     */
    private void sendUserEmail(User user, String template, String subject, String message, String url) {
        mailMessage.setSubject("[" + messages.getMessage("webapp.name") + "] " + subject);
        mailMessage.setTo(user.getEmail());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("message", message);
        model.put("URL", url);

        mailEngine.sendMessage(mailMessage, template, model);
    }

    /**
     * {@inheritDoc}
     */
    @Autowired
    @Override
    public void setUserDao(UserDao userDao) {
        this.dao = userDao;
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
     * メッセージソースを設定する.
     *
     * @param messageSource
     *            メッセージソース
     */
    @Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * パスワード回復案内メールのテンプレートを設定する.
     *
     * @param passwordRecoveryTemplate
     *            パスワード回復案内メールのテンプレート
     */
    public void setPasswordRecoveryTemplate(String passwordRecoveryTemplate) {
        this.passwordRecoveryTemplate = passwordRecoveryTemplate;
    }

    /**
     * パスワード更新メールのテンプレートを設定する.
     *
     * @param passwordUpdatedTemplate
     *            パスワード更新メールのテンプレート
     */
    public void setPasswordUpdatedTemplate(String passwordUpdatedTemplate) {
        this.passwordUpdatedTemplate = passwordUpdatedTemplate;
    }
}
