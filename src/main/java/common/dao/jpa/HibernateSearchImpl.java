package common.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.facet.Facet;

import common.Constants;
import common.dao.HibernateSearch;

/**
 * Hibernate Searchの実装クラス.
 */
public class HibernateSearchImpl<T> implements HibernateSearch<T> {

    /** Entity Managerクラス */
    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    /** エンティティクラス */
    private Class<T> persistentClass;

    /** 形態解析クラス */
    private Analyzer defaultAnalyzer;

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     */
    public HibernateSearchImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        defaultAnalyzer = new StandardAnalyzer();
    }

    /**
     * コンストラクタ.
     *
     * @param persistentClass
     *            エンティティクラス
     * @param entityManager
     *            Entity Managerクラス
     */
    public HibernateSearchImpl(Class<T> persistentClass, EntityManager entityManager) {
        this.persistentClass = persistentClass;
        this.entityManager = entityManager;
        defaultAnalyzer = new StandardAnalyzer();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> search(String[] searchTerm, String[] searchField) {
        return Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchTools.generateQuery(searchTerm, searchField, persistentClass, entityManager, defaultAnalyzer), persistentClass).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> search(String searchTerm) {
        return Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchTools.generateQuery(searchTerm, persistentClass, entityManager, defaultAnalyzer), persistentClass).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Facet> facet(String field, int maxCount) {
        return HibernateSearchTools.generateFacet(field, maxCount, persistentClass, entityManager);
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> searchList(String searchTerm, Integer offset, Integer limit) {
        FullTextQuery query = Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchTools.generateQuery(searchTerm, persistentClass, entityManager, defaultAnalyzer), persistentClass);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long searchCount(String searchTerm) {
        FullTextQuery query = Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchTools.generateQuery(searchTerm, persistentClass, entityManager, defaultAnalyzer), persistentClass);
        return query.getResultSize();
    }
}
