package common.dao.hibernate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.indexes.IndexReaderAccessor;
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

    /** ログ出力クラス */
    private static final Log log = LogFactory.getLog(HibernateSearchTools.class);

    /**
     * プライベート・コンストラクタ.
     * このクラスはインスタンス化禁止.
     */
    private HibernateSearchTools() {
    }

    /**
     * 全文検索クエリを作成する.
     *
     * @param searchTerm
     *            検索文字列
     * @param searchedEntity
     *            エンティティクラス
     * @param sess
     *            DBセッション
     * @return 全文検索クエリ
     */
    public static Query generateQuery(String searchTerm, Class<?> searchedEntity, Session sess) {
        Query query = null;

        if (searchTerm.equals("*")) {
            query = new MatchAllDocsQuery();
        } else {
            IndexReaderAccessor readerAccessor = null;
            IndexReader reader = null;

            try {
                FullTextSession txtSession = Search.getFullTextSession(sess);
                Analyzer analyzer = null;

                if (searchedEntity == null) {
                    analyzer = new StandardAnalyzer();
                } else {
                    analyzer = txtSession.getSearchFactory().getAnalyzer(searchedEntity);
                }

                SearchFactory searchFactory = txtSession.getSearchFactory();
                readerAccessor = searchFactory.getIndexReaderAccessor();
                reader = readerAccessor.open(searchedEntity);
                Collection<String> fieldNames = new HashSet<String>();

                for (FieldInfo fieldInfo : MultiFields.getMergedFieldInfos(reader)) {
                    if (fieldInfo.getIndexOptions() != IndexOptions.NONE) {
                        fieldNames.add(fieldInfo.name);
                    }
                }

                fieldNames.remove("_hibernate_class");
                String[] fnames = fieldNames.toArray(new String[0]);
                String[] queries = new String[fnames.length];

                for (int i = 0; i < queries.length; i++) {
                    queries[i] = searchTerm;
                }

                query = MultiFieldQueryParser.parse(queries, fnames, analyzer);
            } catch (ParseException e) {
                throw new SearchException(e);
            } finally {
                if (readerAccessor != null && reader != null) {
                    readerAccessor.close(reader);
                }
            }
        }

        return query;
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
     * @param sess
     *            DBセッション
     * @return ファセットのリスト
     */
    public static List<Facet> generateFacet(String field, int maxCount, Class<?> searchedEntity, Session sess) {
        FullTextSession txtSession = Search.getFullTextSession(sess);
        SearchFactory searchFactory = txtSession.getSearchFactory();
        QueryBuilder builder = searchFactory.buildQueryBuilder().forEntity(searchedEntity).get();

        FacetingRequest categoryFacetingRequest = builder.facet()
                .name(field + searchedEntity.getSimpleName()).onField(field).discrete()
                .orderedBy(FacetSortOrder.COUNT_DESC).includeZeroCounts(false).maxFacetCount(maxCount)
                .createFacetingRequest();

        Query luceneQuery = builder.all().createQuery();
        FullTextQuery fullTextQuery = txtSession.createFullTextQuery(luceneQuery);
        FacetManager facetManager = fullTextQuery.getFacetManager();
        facetManager.enableFaceting(categoryFacetingRequest);

        return facetManager.getFacets(field + searchedEntity.getSimpleName());
    }

    /**
     * インデックスを再作成する.
     *
     * @param clazz
     *            エンティティクラス
     * @param sess
     *            DBセッション
     */
    public static void reindex(Class<?> clazz, Session sess) {
        FullTextSession txtSession = Search.getFullTextSession(sess);
        MassIndexer massIndexer = txtSession.createIndexer(clazz);

        try {
            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            log.error("mass reindexing interrupted: " + e.getMessage());
        } finally {
            txtSession.flushToIndexes();
        }
    }

    /**
     * 全てのインデックスを再作成する.
     *
     * @param async
     *            true:非同期、false:同期
     * @param sess
     *            DBセッション
     */
    public static void reindexAll(boolean async, Session sess) {
        FullTextSession txtSession = Search.getFullTextSession(sess);
        MassIndexer massIndexer = txtSession.createIndexer();
        massIndexer.purgeAllOnStart(true);

        try {
            if (!async) {
                massIndexer.startAndWait();
            } else {
                massIndexer.start();
            }
        } catch (InterruptedException e) {
            log.error("mass reindexing interrupted: " + e.getMessage());
        } finally {
            txtSession.flushToIndexes();
        }
    }
}
