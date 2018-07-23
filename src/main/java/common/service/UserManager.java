package common.service;

import java.util.List;

import common.dto.PasswordForm;
import common.dto.SignupUserForm;
import common.dto.UploadForm;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.model.PaginatedList;
import common.model.User;

/**
 * ユーザ処理のインターフェース.
 */
public interface UserManager {

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
     * 指定された新規登録ユーザを永続化する.
     *
     * @param signupUser
     *            新規登録ユーザ
     * @return 永続化されたユーザ
     */
    User saveSignupUser(SignupUserForm signupUser);

    /**
     * 指定された新規登録ユーザを有効にする.
     *
     * @param user
     *            ユーザ
     * @return 永続化されたユーザ
     */
    User enableUser(User user);

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
     * 指定されたユーザのパスワードを更新する.
     *
     * @param passwordForm
     *            パスワード情報
     *
     * @return 永続化されたユーザ
     */
    User updatePassword(PasswordForm passwordForm);

    /**
     * オブジェクトをページング処理して取得する.
     *
     * @param userSearchCriteria
     *            検索オブジェクト
     * @param page
     *            表示するページの番号
     * @return ページング情報保持モデル
     */
    PaginatedList<UserSearchResults> createPaginatedList(UserSearchCriteria userSearchCriteria, Integer page);

    /**
     * インデックスを再作成する.
     */
    void reindex();
}
