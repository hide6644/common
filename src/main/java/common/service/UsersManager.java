package common.service;

import java.util.List;

import common.dto.PaginatedList;
import common.dto.UploadForm;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.User;

/**
 * 複数ユーザ処理のインターフェース.
 */
public interface UsersManager {

    /**
     * 全ユーザを取得する.
     *
     * @return ユーザのリスト
     */
    List<User> getUsers();

    /**
     * アップロードする.
     *
     * @param uploadForm
     *            アップロードファイルの情報
     */
    void uploadUsers(UploadForm uploadForm);

    /**
     * オブジェクトをページング処理して取得する.
     *
     * @param userSearchCriteria
     *            ユーザ検索条件
     * @param page
     *            表示するページの番号
     * @return ページング処理、情報
     */
    PaginatedList<UserSearchResults> createPaginatedList(UserSearchCriteria userSearchCriteria, Integer page);

    /**
     * オブジェクトをページング処理して取得する.
     *
     * @param userSearchCriteria
     *            ユーザ検索条件
     * @param page
     *            表示するページの番号
     * @return ページング処理、情報
     */
    PaginatedList<UserSearchResults> createPaginatedListByFullText(UserSearchCriteria userSearchCriteria, Integer page);

    /**
     * インデックスを再作成する.
     */
    void reindex();
}
