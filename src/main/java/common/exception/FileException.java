package common.exception;

/**
 * ファイル例外.
 *
 * @author hide6644
 */
public class FileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ.
     *
     * @param message
     *            エラーメッセージ
     */
    public FileException(String message) {
        super(message);
    }

    /**
     * コンストラクタ.
     *
     * @param message
     *            エラーメッセージ
     * @param cause
     *            例外オブジェクト
     */
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}
