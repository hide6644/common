package common.service;

import common.model.User;

/**
 * パスワードトークン処理のインターフェース.
 */
public interface PasswordTokenManager {

    /**
     * トークンを生成する.
     *
     * @param user
     *            ユーザ
     * @return トークン
     */
    String generateRecoveryToken(User user);

    /**
     * トークンが一致するか確認する.
     *
     * @param user
     *            ユーザ
     * @param token
     *            トークン
     * @return true:一致、false:不一致
     */
    boolean isRecoveryTokenValid(User user, String token);
}
