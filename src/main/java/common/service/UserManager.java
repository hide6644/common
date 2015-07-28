package common.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import common.dao.UserDao;
import common.model.User;
import common.webapp.form.UploadForm;

/**
 * ユーザ処理のインターフェース.
 */
public interface UserManager extends GenericManager<User, Long> {

    /**
     * 全ユーザを取得する.
     *
     * @return ユーザのリスト
     */
    List<User> getUsers();

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
     * 指定されたユーザを永続化する.
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
     * アップロードする.
     *
     * @param uploadForm
     *            アップロードファイルの情報
     */
    void uploadUsers(UploadForm uploadForm);

    /**
     * 指定されたアップロードユーザを永続化する.
     *
     * @param user
     *            ユーザ
     * @return 永続化されたユーザ
     */
    User saveUploadUser(User user);

    /**
     * 指定された新規登録ユーザを永続化する.
     *
     * @param user
     *            ユーザ
     * @return 永続化されたユーザ
     */
    User saveSignupUser(User user);

    /**
     * 指定された新規登録ユーザを有効にする.
     *
     * @param user
     *            ユーザ
     * @return 永続化されたユーザ
     */
    User enableUser(User user);

    /**
     * ユーザ本登録用のURLをメールで送信する.
     *
     * @param user
     *            ユーザ
     * @param urlTemplate
     *            ユーザ本登録用のURL
     */
    void sendSignupUserEmail(User user, String urlTemplate);

    /**
     * ユーザ登録用のURLをメールで送信する.
     *
     * @param user
     *            ユーザ
     * @param urlTemplate
     *            ユーザ登録用のURL
     */
    void sendCreatedUserEmail(User user, String urlTemplate);

    /**
     * パスワード回復用のURLを生成する.
     *
     * @param user
     *            ユーザ
     * @param urlTemplate
     *            生成元となるURL
     * @return パスワード再入力用のURL
     */
    String buildRecoveryPasswordUrl(User user, String urlTemplate);

    /**
     * リカバリートークンを生成する.
     *
     * @param user
     *            ユーザ
     * @return リカバリートークン
     */
    String generateRecoveryToken(User user);

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
     * パスワード再入力用のURLをメールで送信する.
     *
     * @param username
     *            ユーザ名
     * @param urlTemplate
     *            パスワード再入力用のURL
     */
    void sendPasswordRecoveryEmail(String username, String urlTemplate);

    /**
     * 指定されたユーザ名のユーザのパスワードを更新する.
     *
     * @param username
     *            ユーザ名
     * @param currentPassword
     *            現在のパスワード
     * @param recoveryToken
     *            リカバリートークン
     * @param newPassword
     *            新しいパスワード
     * @param applicationUrl
     *            当アプリケーションのURL
     * @return パスワード更新後のユーザ
     */
    User updatePassword(String username, String currentPassword, String recoveryToken, String newPassword, String applicationUrl);

    /**
     * 指定されたユーザの権限を永続オブジェクトする.
     *
     * @param user
     *            ユーザ
     */
    void activateRoles(User user);

    /**
     * ユーザDAOを設定する.
     *
     * @param userDao
     *            ユーザDAO
     */
    void setUserDao(UserDao userDao);

    /**
     * パスワードエンコーダーを設定する.
     *
     * @param passwordEncoder
     *            パスワードエンコーダー
     */
    void setPasswordEncoder(PasswordEncoder passwordEncoder);
}
