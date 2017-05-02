package common.service.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.search.query.facet.Facet;

import common.dao.GenericDao;
import common.service.GenericManager;

/**
 * 一般的なCRUD POJOsの実装クラス.
 */
public class GenericManagerImpl<T, PK extends Serializable> implements GenericManager<T, PK> {

    /** ログ出力クラス */
    protected Logger log = LogManager.getLogger(getClass());

    /** 一般的なCRUD DAOのインターフェース */
    protected GenericDao<T, PK> dao;

    /**
     * デフォルト・コンストラクタ.
     */
    public GenericManagerImpl() {
    }

    /**
     * コンストラクタ.
     *
     * @param genericDao
     *            一般的なCRUD DAOのインターフェース
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
    public List<T> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().length() == 0) {
            return getAll();
        }

        return dao.search(searchTerm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Facet> facet(String field, int max) {
        return dao.facet(field, max);
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
}
