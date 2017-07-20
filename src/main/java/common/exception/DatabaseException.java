package common.exception;

/**
 * データベース例外.
 */
public class DatabaseException extends RuntimeException {

    /**
     * コンストラクタ.
     *
     * @param message
     *            エラーメッセージ
     * @param cause
     *            例外オブジェクト
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
