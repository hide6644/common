package common.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.search.query.facet.Facet;

/**
 * 一般的なCRUD POJOsのインターフェイス.
 */
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
     * オブジェクトのインデックス済み項目を全文検索する.
     *
     * @param searchTerm
     *            検索文字列
     * @return 検索結果のオブジェクトのリスト
     */
    List<T> search(String searchTerm);

    /**
     * ファセットを作成する.
     *
     * @param field
     *            対象となる項目
     * @param max
     *            ファセットの件数
     * @return ファセットのリスト
     */
    List<Facet> facet(String field, int max);

    /**
     * インデックスを再作成する.
     */
    void reindex();

    /**
     * 全てのインデックスを再作成する.
     *
     * @param async
     *            true:非同期、false:同期
     */
    void reindexAll(boolean async);
}
