package common.exception;

/**
 * データベース例外.
 *
 * @author hide6644
 */
public class DBException extends RuntimeException {

    /**
     * コンストラクタ.
     *
     * @param message
     *            エラーメッセージ
     * @param cause
     *            例外オブジェクト
     */
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
