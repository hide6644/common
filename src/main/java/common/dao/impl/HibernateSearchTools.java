package common.dao.impl;

import jakarta.persistence.EntityManager;

import org.hibernate.search.mapper.orm.Search;
import lombok.extern.log4j.Log4j2;

/**
 * 全文検索を処理するクラス.
 */
@Log4j2
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
        var searchSession = Search.session(entityManager);
        var massIndexer = searchSession.massIndexer(clazz);

        try {
            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            log.warn("mass reindexing interrupted:{}", () -> e.getMessage());
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
        var searchSession = Search.session(entityManager);
        var massIndexer = searchSession.massIndexer();
        massIndexer.purgeAllOnStart(true);

        if (!async) {
            try {
                massIndexer.startAndWait();
            } catch (InterruptedException e) {
                log.warn("mass reindexing interrupted:{}", () -> e.getMessage());
                Thread.currentThread().interrupt();
            }
        } else {
            massIndexer.start();
        }
    }
}
