package common.dao.jpa;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.hibernate.search.query.facet.FacetingRequest;

import common.dao.SearchException;

/**
 * 全文検索クエリを処理するクラス.
 */
class HibernateSearchTools {

    /**
     * プライベート・コンストラクタ.
     */
    private HibernateSearchTools() {
    }

    /**
     * 全文検索クエリを作成する.
     *
     * @param searchTerms
     *            検索文字列
     * @param searchFields
     *            検索項目
     * @param searchFlags
     *            検索項目のフラグ
     * @param searchedEntity
     *            エンティティクラス
     * @param entityManager
     *            Entity Manager
     * @param defaultAnalyzer
     *            形態解析クラス
     * @return 全文検索クエリ
     */
    public static Query generateQuery(String[] searchTerms, String[] searchFields, Occur[] searchFlags, Class<?> searchedEntity, EntityManager entityManager, Analyzer defaultAnalyzer) {
        try {
            return MultiFieldQueryParser.parse(searchTerms, searchFields, searchFlags,
                    Optional.ofNullable(searchedEntity)
                            .map(entity -> Search.getFullTextEntityManager(entityManager).getSearchFactory().getAnalyzer(entity))
                            .orElse(defaultAnalyzer));
        } catch (ParseException e) {
            throw new SearchException(e);
        }
    }

    /**
     * 全文検索クエリを作成する.
     *
     * @param searchTerms
     *            検索文字列
     * @param searchFields
     *            検索項目
     * @param searchedEntity
     *            エンティティクラス
     * @param entityManager
     *            Entity Manager
     * @param defaultAnalyzer
     *            形態解析クラス
     * @return 全文検索クエリ
     */
    public static Query generateQuery(String[] searchTerms, String[] searchFields, Class<?> searchedEntity, EntityManager entityManager, Analyzer defaultAnalyzer) {
        try {
            return MultiFieldQueryParser.parse(searchTerms, searchFields,
                    Optional.ofNullable(searchedEntity)
                            .map(entity -> Search.getFullTextEntityManager(entityManager).getSearchFactory().getAnalyzer(entity))
                            .orElse(defaultAnalyzer));
        } catch (ParseException e) {
            throw new SearchException(e);
        }
    }

    /**
     * 全文検索クエリを作成する.
     *
     * @param searchTerm
     *            検索文字列
     * @param searchedEntity
     *            エンティティクラス
     * @param entityManager
     *            Entity Manager
     * @param defaultAnalyzer
     *            形態解析クラス
     * @return 全文検索クエリ
     */
    public static Query generateQuery(String searchTerm, Class<?> searchedEntity, EntityManager entityManager, Analyzer defaultAnalyzer) {
        if (searchTerm.equals("*")) {
            return new MatchAllDocsQuery();
        }

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
        IndexReaderAccessor readerAccessor = searchFactory.getIndexReaderAccessor();
        IndexReader reader = readerAccessor.open(searchedEntity);

        try {
            String[] fnames = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            MultiFields.getMergedFieldInfos(reader).iterator(), Spliterator.ORDERED),
                    false)
                    .filter(fieldInfo -> fieldInfo.getIndexOptions() != IndexOptions.NONE)
                    .filter(fieldInfo -> !fieldInfo.name.equals("_hibernate_class"))
                    .map(fieldInfo -> fieldInfo.name)
                    .distinct()
                    .toArray(size -> new String[size]);

            String[] queries = new String[fnames.length];
            Arrays.fill(queries, searchTerm);

            return generateQuery(queries, fnames, searchedEntity, entityManager, defaultAnalyzer);
        } finally {
            if (readerAccessor != null && reader != null) {
                readerAccessor.close(reader);
            }
        }
    }

    /**
     * ファセットクエリを作成する.
     *
     * @param field
     *            対象となる項目
     * @param maxCount
     *            ファセットの最大件数
     * @param searchedEntity
     *            エンティティクラス
     * @param entityManager
     *            Entity Manager
     * @return ファセットのリスト
     */
    public static List<Facet> generateFacet(String field, int maxCount, Class<?> searchedEntity, EntityManager entityManager) {
        FullTextEntityManager txtentityManager = Search.getFullTextEntityManager(entityManager);
        SearchFactory searchFactory = txtentityManager.getSearchFactory();
        QueryBuilder builder = searchFactory.buildQueryBuilder().forEntity(searchedEntity).get();

        FacetingRequest categoryFacetingRequest = builder.facet()
                .name(field + searchedEntity.getSimpleName()).onField(field).discrete()
                .orderedBy(FacetSortOrder.COUNT_DESC).includeZeroCounts(false).maxFacetCount(maxCount)
                .createFacetingRequest();

        Query luceneQuery = builder.all().createQuery();
        FullTextQuery fullTextQuery = txtentityManager.createFullTextQuery(luceneQuery);
        FacetManager facetManager = fullTextQuery.getFacetManager();
        facetManager.enableFaceting(categoryFacetingRequest);

        return facetManager.getFacets(field + searchedEntity.getSimpleName());
    }

    /**
     * インデックスを再作成する.
     *
     * @param clazz
     *            エンティティクラス
     * @param entityManager
     *            Entity Manager
     */
    public static void reindex(Class<?> clazz, EntityManager entityManager) {
        FullTextEntityManager txtentityManager = Search.getFullTextEntityManager(entityManager);
        MassIndexer massIndexer = txtentityManager.createIndexer(clazz);

        try {
            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            LogManager.getLogger(HibernateSearchTools.class).warn("mass reindexing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            txtentityManager.flushToIndexes();
        }
    }

    /**
     * 全てのインデックスを再作成する.
     *
     * @param async
     *            true:非同期、false:同期
     * @param entityManager
     *            Entity Manager
     */
    public static void reindexAll(boolean async, EntityManager entityManager) {
        FullTextEntityManager txtentityManager = Search.getFullTextEntityManager(entityManager);
        MassIndexer massIndexer = txtentityManager.createIndexer();
        massIndexer.purgeAllOnStart(true);

        try {
            if (!async) {
                massIndexer.startAndWait();
            } else {
                massIndexer.start();
            }
        } catch (InterruptedException e) {
            LogManager.getLogger(HibernateSearchTools.class).warn("mass reindexing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            txtentityManager.flushToIndexes();
        }
    }
}
