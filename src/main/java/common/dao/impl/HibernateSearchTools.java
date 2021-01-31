package common.dao.impl;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;

/**
 * 全文検索を処理するクラス.
 */
class HibernateSearchTools {

    /**
     * プライベート・コンストラクタ.
     */
    private HibernateSearchTools() {
    }

    /**
     * インデックスを再作成する.
     *
     * @param clazz
     *            エンティティクラス
     * @param entityManager
     *            Entity Manager
     */
    public static <T> void reindex(Class<T> clazz, EntityManager entityManager) {
        SearchSession searchSession = Search.session(entityManager);
        MassIndexer massIndexer = searchSession.massIndexer(clazz);

        try {
            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            LogManager.getLogger(HibernateSearchTools.class).warn("mass reindexing interrupted:{}", () -> e.getMessage());
            Thread.currentThread().interrupt();
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
        SearchSession searchSession = Search.session(entityManager);
        MassIndexer massIndexer = searchSession.massIndexer();
        massIndexer.purgeAllOnStart(true);

        if (!async) {
            try {
                massIndexer.startAndWait();
            } catch (InterruptedException e) {
                LogManager.getLogger(HibernateSearchTools.class).warn("mass reindexing interrupted:{}", () -> e.getMessage());
                Thread.currentThread().interrupt();
            }
        } else {
            massIndexer.start();
        }
    }
}
