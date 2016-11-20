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
     * @param paginatedList
     *            ページング情報保持モデル
     */
    void createList(PaginatedList<T> paginatedList);
}
