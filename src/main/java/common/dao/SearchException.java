package common.dao;

/**
 * 全文検索処理の例外クラス.
 */
public class SearchException extends RuntimeException {

    /**
     * コンストラクタ
     *
     * @param e
     *            例外クラス
     */
    public SearchException(Throwable e) {
        super(e);
    }
}
