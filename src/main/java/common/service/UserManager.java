package common.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import common.dao.UserDao;
import common.model.User;

public interface UserManager extends GenericManager<User, Long> {

    void setUserDao(UserDao userDao);

    void setPasswordEncoder(PasswordEncoder passwordEncoder);

    User getUser(String userId);

    User getUserByUsername(String username) throws UsernameNotFoundException;

    List<User> getUsers();

    User saveUser(User user);

    void removeUser(User user);

    void removeUser(String userId);

    List<User> search(String searchTerm);

    String buildRecoveryPasswordUrl(User user, String urlTemplate);

    String generateRecoveryToken(User user);

    boolean isRecoveryTokenValid(String username, String token);

    boolean isRecoveryTokenValid(User user, String token);

    void sendPasswordRecoveryEmail(String username, String urlTemplate);

    User updatePassword(String username, String currentPassword, String recoveryToken, String newPassword, String applicationUrl);
}
