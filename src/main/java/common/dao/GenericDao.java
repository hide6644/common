package common.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.search.query.facet.Facet;

/**
 * 一般的なCRUD DAOのインターフェース.
 */
public interface GenericDao<T, PK extends Serializable> {

    /**
     * すべてのオブジェクトを取得する.
     *
     * @return モデル一覧
     */
    List<T> getAll();

    /**
     * 重複を除くすべてのオブジェクトを取得する.
     *
     * @return 重複を除くモデル一覧
     */
    List<T> getAllDistinct();

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
     * 指定されたクエリを使用し検索する.
     *
     * @param queryName
     *            クエリの名称
     * @param queryParams
     *            クエリのパラメータ
     * @return 検索結果のオブジェクトのリスト
     */
    List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams);

    /**
     * 全文検索する.
     *
     * @param searchTerm
     *            検索文字列
     * @return 検索結果のオブジェクトのリスト
     */
    List<T> search(String searchTerm) throws SearchException;

    /**
     * ファセットを作成する.
     *
     * @param field
     *            対象となる項目
     * @param max
     *            ファセットの最大件数
     * @return ファセットのリスト
     */
    List<Facet> facet(String field, int maxCount);

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
     * 指定の範囲のオブジェクトを取得する.
     *
     * @param search
     *            検索条件
     * @param offset
     *            開始位置
     * @param limit
     *            取得数
     * @return 職種一覧
     */
    List<T> getPaged(Serializable search, Integer offset, Integer limit);

    /**
     * 件数を取得する.
     *
     * @param search
     *            検索条件
     * @return 件数
     */
    int getRecordCount(Serializable search);
}
