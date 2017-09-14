package common.webapp.form;

/**
 * 取り込みエラーの情報を保持するクラス.
 */
public class UploadError {

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

    public int getRowNo() {
        return rowNo;
    }

    /**
     * 行番号を設定する.
     *
     * @param rowNo
     *            行番号
     */
    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * 項目名を設定する.
     *
     * @param fieldName
     *            項目名
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    /**
     * メッセージを設定する.
     *
     * @param message
     *            メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
