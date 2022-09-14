package common.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import common.Constants;
import common.dao.HibernateSearch;

/**
 * Hibernate Searchの実装クラス.
 */
public class HibernateSearchImpl<T> implements HibernateSearch {

    /** Entity Managerクラス */
    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    /** エンティティクラス */
    private Class<T> persistentClass;

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     */
    public HibernateSearchImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindex() {
        HibernateSearchTools.reindex(persistentClass, entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAll(boolean async) {
        HibernateSearchTools.reindexAll(async, entityManager);
    }
}
