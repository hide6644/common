package common.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.GenericDao;
import common.model.PaginatedList;
import common.service.GenericManager;

/**
 * 一般的なロジッククラス.
 */
public class GenericManagerImpl<T, PK extends Serializable> implements GenericManager<T, PK> {

    /** ログ出力クラス */
    protected final Log log = LogFactory.getLog(getClass());

    /** 一般的なCRUD DAO */
    protected GenericDao<T, PK> dao;

    /**
     * デフォルト・コンストラクタ
     */
    public GenericManagerImpl() {
    }

    /**
     * コンストラクタ
     *
     * @param genericDao
     *            一般的なCRUD DAO
     */
    public GenericManagerImpl(GenericDao<T, PK> genericDao) {
        this.dao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(PK id) {
        return dao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(PK id) {
        return dao.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T save(T object) {
        return dao.save(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(T object) {
        dao.remove(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(PK id) {
        dao.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> search(String q) {
        if (q == null || "".equals(q.trim())) {
            return getAll();
        }

        return dao.search(q);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindex() {
        dao.reindex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAll(boolean async) {
        dao.reindexAll(async);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getPaged(Class<?> searchClass, PaginatedList<T> paginatedList) {
        paginatedList.setAllRecordCount(dao.getRecordCount(searchClass, paginatedList.getSearchCondition()));

        if (paginatedList.getAllRecordCount() == 0) {
            return new ArrayList<T>();
        }

        // 指定されたページ数より、ページ総数が小さかった場合
        if (paginatedList.getCurrentPageNumber() > paginatedList.getAllPageCount()) {
            paginatedList.setCurrentPageNumber(paginatedList.getAllPageCount());
        }

        return dao.getPaged(searchClass, paginatedList.getSearchCondition(), paginatedList.getCurrentStartRecordNumber(), paginatedList.getPageSize());
    }
}
