package common.exception;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * データベースアプリケーション例外.<br />
 * アプリ仕様上問題があるデータを処理した場合に発生させる例外.
 *
 * @author hide6644
 */
public class DBCheckException extends DBException {

    private static final long serialVersionUID = 1L;

    /** エラーメッセージ一覧 */
    List<Map<String, Serializable>> errors;

    /**
     * コンストラクタ.
     *
     * @param errors
     *            エラーメッセージ一覧
     */
    public DBCheckException(List<Map<String, Serializable>> errors) {
        super(null, null);
        this.errors = errors;
    }

    /**
     * エラーメッセージ取得.
     *
     * @return エラーメッセージ
     */
    public List<Map<String, Serializable>> getAllErrors() {
        return errors;
    }
}
