package common.service.impl;

import java.io.Serializable;
import java.util.ArrayList;

import common.dao.PaginatedDao;
import common.model.PaginatedList;
import common.service.PaginatedManager;

/**
 * Paginated List ロジッククラス.
 */
public class PaginatedManagerImpl<T, PK extends Serializable> extends GenericManagerImpl<T, PK> implements PaginatedManager<T, PK> {

    /** Paginated List DAO */
    protected PaginatedDao<T, PK> paginatedDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createList(PaginatedList<T> paginatedList) {
        paginatedList.setAllRecordCount(paginatedDao.getCount(paginatedList.getSearchCondition()));

        if (paginatedList.getAllRecordCount() == 0) {
            paginatedList.setCurrentPage(new ArrayList<T>());
            return;
        }

        // 指定されたページ数より、ページ総数が小さかった場合
        if (paginatedList.getCurrentPageNumber() > paginatedList.getAllPageCount()) {
            paginatedList.setCurrentPageNumber(paginatedList.getAllPageCount());
        }

        paginatedList.setCurrentPage(paginatedDao.getList(paginatedList.getSearchCondition(), paginatedList.getCurrentStartRecordNumber() - 1, paginatedList.getPageSize()));
    }
}
