package common.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import common.Constants;
import common.dao.HibernateSearch;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Hibernate Searchの実装クラス.
 */
@RequiredArgsConstructor
public class HibernateSearchImpl<T> implements HibernateSearch {

    /** Entity Managerクラス */
    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    /** エンティティクラス */
    @NonNull
    private Class<T> persistentClass;

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
