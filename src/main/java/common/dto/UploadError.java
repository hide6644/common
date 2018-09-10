package common.dto;

import java.io.Serializable;

/**
 * 取り込みエラーの情報を保持するクラス.
 */
public final class UploadError implements Serializable {

    /** 行番号 */
    private int rowNo;

    /** 項目名 */
    private String fieldName;

    /** メッセージ */
    private String message;

    /**
     * コンストラクタ
     *
     * @param rowNo
     *            行番号
     * @param fieldName
     *            項目名
     * @param message
     *            メッセージ
     */
    public UploadError(int rowNo, String fieldName, String message) {
        this.rowNo = rowNo;
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * 行番号を取得する.
     *
     * @return 行番号
     */
    public int getRowNo() {
        return rowNo;
    }

    /**
     * 項目名を取得する.
     *
     * @return 項目名
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * メッセージを取得する.
     *
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }
}
