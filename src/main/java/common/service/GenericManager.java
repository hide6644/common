package common.service;

import java.io.Serializable;
import java.util.List;

import common.model.PaginatedList;

public interface GenericManager<T, PK extends Serializable> {

    /**
     * 全オブジェクトを取得する.
     *
     * @return オブジェクトのリスト
     */
    List<T> getAll();

    /**
     * 指定されたキーのオブジェクトを取得する.
     *
     * @param id
     *            主キー項目
     * @return オブジェクト
     */
    T get(PK id);

    /**
     * 指定されたキーのオブジェクトが存在するか.
     *
     * @param id
     *            主キー項目
     * @return オブジェクト
     */
    boolean exists(PK id);

    /**
     * 指定されたオブジェクトを永続化する.
     *
     * @param object
     *            永続化するオブジェクト
     * @return 永続化されたオブジェクト
     */
    T save(T object);

    /**
     * 指定されたオブジェクトを削除する.
     *
     * @param object
     *            削除するオブジェクト
     */
    void remove(T object);

    /**
     * 指定されたキーのオブジェクトを削除する.
     *
     * @param id
     *            主キー項目
     */
    void remove(PK id);

    /**
     * 全文検索する.
     *
     * @param searchTerm
     *            検索文字列
     * @return 検索結果のオブジェクトのリスト
     */
    List<T> search(String searchTerm);

    /**
     * インデックスを再作成する.
     */
    void reindex();

    /**
     * 全てのインデックスを再作成する.
     *
     * @param async
     *            true:非同期
     */
    void reindexAll(boolean async);

    /**
     * オブジェクトをページング処理して取得する.
     *
     * @param paginatedList
     *            ページング情報保持モデル
     * @return ページング処理された分類一覧
     */
    List<T> getPaged(PaginatedList<T> paginatedList);
}
