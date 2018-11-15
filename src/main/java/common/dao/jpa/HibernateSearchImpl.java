package common.dao.jpa;

import java.util.List;
import java.util.stream.Stream;

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
    public Stream<T> search(String[] searchTerm, String[] searchField) {
        return createFullTextQuery(searchTerm, searchField).getResultStream();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Stream<T> search(String searchTerm) {
        return createFullTextQuery(searchTerm).getResultStream();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> search(String[] searchTerm, String[] searchField, Integer offset, Integer limit) {
        FullTextQuery query = createFullTextQuery(searchTerm, searchField);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> search(String searchTerm, Integer offset, Integer limit) {
        FullTextQuery query = createFullTextQuery(searchTerm);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(String[] searchTerm, String[] searchField) {
        return createFullTextQuery(searchTerm, searchField).getResultSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(String searchTerm) {
        return createFullTextQuery(searchTerm).getResultSize();
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
     * 全文検索クエリを取得する.
     *
     * @param searchTerm
     *            検索文字列
     * @param searchField
     *            検索項目
     * @return 全文検索クエリ
     */
    private FullTextQuery createFullTextQuery(String[] searchTerm, String[] searchField) {
        return Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchTools.generateQuery(searchTerm, searchField, persistentClass, entityManager, defaultAnalyzer), persistentClass);
    }

    /**
     * 全文検索クエリを取得する.
     *
     * @param searchTerm
     *            検索文字列
     * @return 全文検索クエリ
     */
    private FullTextQuery createFullTextQuery(String searchTerm) {
        return Search.getFullTextEntityManager(entityManager).createFullTextQuery(HibernateSearchTools.generateQuery(searchTerm, persistentClass, entityManager, defaultAnalyzer), persistentClass);
    }
}
