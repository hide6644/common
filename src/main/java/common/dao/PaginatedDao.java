package common.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Paginated List DAOのインターフェース.
 */
public interface PaginatedDao<T, K extends Serializable> extends GenericDao<T, K> {

    /**
     * 指定の範囲のオブジェクトを取得する.
     *
     * @param searchCondition
     *            検索条件
     * @param offset
     *            開始位置
     * @param limit
     *            取得数
     * @return 検索結果のオブジェクトのリスト
     */
    public List<T> getList(Object searchCondition, Integer offset, Integer limit);

    /**
     * 件数を取得する.
     *
     * @param searchCondition
     *            検索条件
     * @return 件数
     */
    public long getCount(Object searchCondition);

    /**
     * 指定の範囲のオブジェクトを取得(全文検索)する.
     *
     * @param searchTerm
     *            検索文字列
     * @param offset
     *            開始位置
     * @param limit
     *            取得数
     * @return 検索結果のオブジェクトのリスト
     */
    public List<T> searchList(String searchTerm, Integer offset, Integer limit) ;

    /**
     * 件数を取得(全文検索)する.
     *
     * @param searchTerm
     *            検索文字列
     * @return 件数
     */
    public long searchCount(String searchTerm);
}
