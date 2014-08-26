package common.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.GenericDao;
import common.model.PaginatedList;
import common.service.GenericManager;

public class GenericManagerImpl<T, PK extends Serializable> implements GenericManager<T, PK> {

    protected final Log log = LogFactory.getLog(getClass());

    protected GenericDao<T, PK> dao;

    public GenericManagerImpl() {
    }

    public GenericManagerImpl(GenericDao<T, PK> genericDao) {
        this.dao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    public T get(PK id) {
        return dao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(PK id) {
        return dao.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    public T save(T object) {
        return dao.save(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(T object) {
        dao.remove(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PK id) {
        dao.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> search(String q) {
        if (q == null || "".equals(q.trim())) {
            return getAll();
        }

        return dao.search(q);
    }

    /**
     * {@inheritDoc}
     */
    public void reindex() {
        dao.reindex();
    }

    /**
     * {@inheritDoc}
     */
    public void reindexAll(boolean async) {
        dao.reindexAll(async);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getPaged(Class<?> searchClass, PaginatedList<T> paginatedList) {
        paginatedList.setAllRecordCount(dao.getRecordCount(searchClass, paginatedList.getSearchCondition()));

        if (paginatedList.getAllRecordCount() == 0) {
            return new ArrayList<T>();
        }

        // 指定されたページ数よりページ総数が小さかった場合
        if (paginatedList.getCurrentPageNumber() > paginatedList.getAllPageCount()) {
            paginatedList.setCurrentPageNumber(paginatedList.getAllPageCount());
        }

        return dao.getPaged(searchClass, paginatedList.getSearchCondition(), paginatedList.getCurrentStartRecordNumber(), paginatedList.getPageSize());
    }
}
