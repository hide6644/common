package common.service;

import common.dto.PasswordForm;
import common.dto.SignupUserForm;
import common.dto.UserDetailsForm;
import common.entity.User;

/**
 * ユーザ処理のインターフェース.
 */
public interface UserManager {

    /**
     * 指定されたIDのユーザを取得する.
     *
     * @param userId
     *            ID
     * @return ユーザ
     */
    User getUser(String userId);

    /**
     * 指定されたユーザ名のユーザを取得する.
     *
     * @param username
     *            ユーザ名
     * @return ユーザ
     */
    User getUserByUsername(String username);

    /**
     * 指定されたユーザのユーザ情報を取得する.
     *
     * @param user
     *            ユーザ
     * @return ユーザ情報
     */
    UserDetailsForm getUserDetails(User user);

    /**
     * ユーザを永続化する.
     *
     * @param userDetailsForm
     *            ユーザ情報
     * @return 永続化されたユーザ
     */
    User saveUserDetails(UserDetailsForm userDetailsForm);

    /**
     * ユーザを永続化する.
     *
     * @param user
     *            ユーザ
     * @return 永続化されたユーザ
     */
    User saveUser(User user);

    /**
     * 指定されたユーザを削除する.
     *
     * @param user
     *            ユーザ
     */
    void removeUser(User user);

    /**
     * 指定されたIDのユーザを削除する.
     *
     * @param userId
     *            ID
     */
    void removeUser(String userId);

    /**
     * 新規登録ユーザを永続化する.
     *
     * @param signupUser
     *            新規登録ユーザ
     * @return 永続化されたユーザ
     */
    User saveSignupUser(SignupUserForm signupUser);

    /**
     * 新規登録ユーザを有効にする.
     *
     * @param username
     *            ユーザ名
     */
    void enableUser(String username);

    /**
     * ユーザをロックする.
     *
     * @param username
     *            ユーザ名
     */
    void lockoutUser(String username);

    /**
     * リカバリートークンが一致するか確認する.
     *
     * @param username
     *            ユーザ名
     * @param token
     *            リカバリートークン
     * @return true:一致、false:不一致
     */
    boolean isRecoveryTokenValid(String username, String token);

    /**
     * リカバリートークンが一致するか確認する.
     *
     * @param user
     *            ユーザ
     * @param token
     *            リカバリートークン
     * @return true:一致、false:不一致
     */
    boolean isRecoveryTokenValid(User user, String token);

    /**
     * パスワード回復の処理を実行する.
     *
     * @param username
     *            ユーザ名
     */
    void recoveryPassword(String username);

    /**
     * ユーザのパスワードを更新する.
     *
     * @param passwordForm
     *            パスワード情報
     *
     * @return 永続化されたユーザ
     */
    User updatePassword(PasswordForm passwordForm);

    /**
     * インデックスを再作成する.
     */
    void reindex();
}
