package common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.dao.UserDao;
import common.exception.DBException;
import common.model.User;
import common.service.MailEngine;
import common.service.PasswordTokenManager;
import common.service.UserManager;
import common.service.UserService;

@Service("userManager")
@WebService(serviceName = "UserService", endpointInterface = "common.service.UserService")
public class UserManagerImpl extends GenericManagerImpl<User, Long> implements UserManager, UserService {

    private PasswordEncoder passwordEncoder;

    private UserDao userDao;

    private MailEngine mailEngine;

    private SimpleMailMessage message;

    private PasswordTokenManager passwordTokenManager;

    private String passwordRecoveryTemplate = "passwordRecovery.vm";

    private String passwordUpdatedTemplate = "passwordUpdated.vm";

    @Autowired
    @Qualifier("passwordEncoder")
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.dao = userDao;
        this.userDao = userDao;
    }

    @Autowired(required = false)
    public void setMailEngine(MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    @Autowired(required = false)
    public void setMailMessage(SimpleMailMessage message) {
        this.message = message;
    }

    @Autowired(required = false)
    public void setPasswordTokenManager(PasswordTokenManager passwordTokenManager) {
        this.passwordTokenManager = passwordTokenManager;
    }

    public void setPasswordRecoveryTemplate(String passwordRecoveryTemplate) {
        this.passwordRecoveryTemplate = passwordRecoveryTemplate;
    }

    public void setPasswordUpdatedTemplate(String passwordUpdatedTemplate) {
        this.passwordUpdatedTemplate = passwordUpdatedTemplate;
    }

    @Override
    public User getUser(String userId) {
        return userDao.get(new Long(userId));
    }

    @Override
    public List<User> getUsers() {
        return userDao.getAllDistinct();
    }

    @Override
    public User saveUser(User user) {
        if (user.getVersion() == null) {
            // if new user, lowercase userId
            user.setUsername(user.getUsername().toLowerCase());
        }

        // Get and prepare password management-related artifacts
        boolean passwordChanged = false;
        if (passwordEncoder != null) {
            // Check whether we have to encrypt (or re-encrypt) the password
            if (user.getVersion() == null) {
                // New user, always encrypt
                passwordChanged = true;
            } else {
                // Existing user, check password in DB
                String currentPassword = userDao.getUserPassword(user.getId());
                if (currentPassword == null) {
                    passwordChanged = true;
                } else {
                    if (!currentPassword.equals(user.getPassword())) {
                        passwordChanged = true;
                    }
                }
            }

            // If password was changed (or new user), encrypt it
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
            throw new DBException("errors.insert", e);
        }
    }

    @Override
    public void removeUser(User user) {
        userDao.remove(user);
    }

    @Override
    public void removeUser(String userId) {
        userDao.remove(new Long(userId));
    }

    @Override
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return (User) userDao.loadUserByUsername(username);
    }

    @Override
    public List<User> search(String searchTerm) {
        return super.search(searchTerm);
    }

    @Override
    public String buildRecoveryPasswordUrl(User user, String urlTemplate) {
        String token = generateRecoveryToken(user);
        String username = user.getUsername();
        return StringUtils.replaceEach(urlTemplate, new String[] { "{username}", "{token}" }, new String[] { username, token });
    }

    @Override
    public String generateRecoveryToken(User user) {
        return passwordTokenManager.generateRecoveryToken(user);
    }

    @Override
    public boolean isRecoveryTokenValid(String username, String token) {
        return isRecoveryTokenValid(getUserByUsername(username), token);
    }

    @Override
    public boolean isRecoveryTokenValid(User user, String token) {
        return passwordTokenManager.isRecoveryTokenValid(user, token);
    }

    @Override
    public void sendPasswordRecoveryEmail(String username, String urlTemplate) {
        User user = getUserByUsername(username);
        String url = buildRecoveryPasswordUrl(user, urlTemplate);

        sendUserEmail(user, passwordRecoveryTemplate, url);
    }

    @Override
    public User updatePassword(String username, String currentPassword, String recoveryToken, String newPassword, String applicationUrl) {
        User user = getUserByUsername(username);

        if (isRecoveryTokenValid(user, recoveryToken)) {
            user.setPassword(newPassword);
            user = saveUser(user);
            passwordTokenManager.invalidateRecoveryToken(user, recoveryToken);

            sendUserEmail(user, passwordUpdatedTemplate, applicationUrl);

            return user;
        } else if (StringUtils.isNotBlank(currentPassword)) {
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(newPassword);
                user = saveUser(user);
                return user;
            }
        }
        // or throw exception
        return null;
    }

    private void sendUserEmail(User user, String template, String url) {
        message.setTo(user.getEmail());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("applicationURL", url);

        mailEngine.sendMessage(message, template, model);
    }
}
