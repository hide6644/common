package common.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Sort;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.facet.Facet;

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
    @Override
    public FullTextQuery search(String[] searchTerms, String[] searchFields, Occur[] searchFlags) {
        return createFullTextQuery(searchTerms, searchFields, searchFlags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullTextQuery search(String[] searchTerms, String[] searchFields) {
        return createFullTextQuery(searchTerms, searchFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullTextQuery search(String searchTerm) {
        return createFullTextQuery(searchTerm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullTextQuery search(String[] searchTerms, String[] searchFields, Long offset, Integer limit, Sort sort) {
        Occur[] searchFlags = new Occur[searchFields.length];
        Arrays.fill(searchFlags, Occur.MUST);
        FullTextQuery query = createFullTextQuery(searchTerms, searchFields, searchFlags);

        query.setFirstResult(offset.intValue());
        query.setMaxResults(limit);
        if (sort != null) {
            query.setSort(sort);
        }

        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullTextQuery search(String searchTerm, Long offset, Integer limit, Sort sort) {
        FullTextQuery query = createFullTextQuery(searchTerm);

        query.setFirstResult(offset.intValue());
        query.setMaxResults(limit);
        if (sort != null) {
            query.setSort(sort);
        }

        return query;
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
     * @param searchTerms
     *            検索文字列
     * @param searchFields
     *            検索項目
     * @param searchFlags
     *            検索項目のフラグ
     * @return 全文検索クエリ
     */
    private FullTextQuery createFullTextQuery(String[] searchTerms, String[] searchFields, Occur[] searchFlags) {
        return Search.getFullTextEntityManager(entityManager)
                .createFullTextQuery(HibernateSearchTools.generateQuery(searchTerms, searchFields, searchFlags, persistentClass, entityManager, defaultAnalyzer), persistentClass);
    }

    /**
     * 全文検索クエリを取得する.
     *
     * @param searchTerms
     *            検索文字列
     * @param searchFields
     *            検索項目
     * @return 全文検索クエリ
     */
    private FullTextQuery createFullTextQuery(String[] searchTerms, String[] searchFields) {
        return Search.getFullTextEntityManager(entityManager)
                .createFullTextQuery(HibernateSearchTools.generateQuery(searchTerms, searchFields, persistentClass, entityManager, defaultAnalyzer), persistentClass);
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
