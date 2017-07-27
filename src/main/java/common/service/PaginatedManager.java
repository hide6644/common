package common.service;

import java.io.Serializable;

import common.model.PaginatedList;

/**
 * Paginated List ロジックのインターフェース.
 */
public interface PaginatedManager<T, PK extends Serializable> extends GenericManager<T, PK> {

    /**
     * オブジェクトをページング処理して取得する.
     *
     * @param searchCondition
     *            検索オブジェクト
     * @param page
     *            表示ページ数
     * @return ページング情報保持モデル
     */
    PaginatedList<T> createPaginatedList(T searchCondition, Integer page);
}
