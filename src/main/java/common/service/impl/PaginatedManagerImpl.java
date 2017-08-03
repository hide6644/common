package common.service.impl;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import common.dao.PaginatedDao;
import common.model.PaginatedList;
import common.service.PaginatedManager;

/**
 * Paginated Listの実装クラス.
 */
public class PaginatedManagerImpl<T, PK extends Serializable> extends GenericManagerImpl<T, PK> implements PaginatedManager<T, PK> {

    /** Paginated List DAO */
    protected PaginatedDao<T, PK> paginatedDao;

    /**
     * デフォルト・コンストラクタ.
     */
    public PaginatedManagerImpl() {
    }

    /**
     * コンストラクタ.
     *
     * @param paginatedDao
     *            Paginated List DAO
     */
    @Autowired
    public PaginatedManagerImpl(PaginatedDao<T, PK> paginatedDao) {
        this.dao = paginatedDao;
        this.paginatedDao = paginatedDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<T> createPaginatedList(T searchCondition, Integer page) {
        PaginatedList<T> paginatedList = new PaginatedList<T>(page);
        paginatedList.setSearchCondition(searchCondition);
        paginatedList.setAllRecordCount(paginatedDao.getCount(paginatedList.getSearchCondition()));

        if (paginatedList.getAllRecordCount() == 0) {
            // 検索結果が0件の場合
            paginatedList.setCurrentPage(new ArrayList<T>());
            return paginatedList;
        }

        if (paginatedList.getCurrentPageNumber() > paginatedList.getAllPageCount()) {
            // 指定されたページ数より、ページ総数が小さかった場合
            paginatedList.setCurrentPageNumber(paginatedList.getAllPageCount());
        }

        paginatedList.setCurrentPage(paginatedDao.getList(paginatedList.getSearchCondition(), paginatedList.getCurrentStartRecordNumber() - 1, paginatedList.getPageSize()));
        return paginatedList;
    }
}
