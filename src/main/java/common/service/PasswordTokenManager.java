package common.service;

import common.model.User;

public interface PasswordTokenManager {

    String generateRecoveryToken(User user);

    boolean isRecoveryTokenValid(User user, String token);

    void invalidateRecoveryToken(User user, String token);
}
