package common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 取り込み結果を保持するクラス.
 */
public class UploadResult implements Serializable {

    /** 取り込み成功総数 */
    private int successTotalCount;

    /** 取り込み中の行番号 */
    private int processingCount;

    /** 取り込みエラー一覧 */
    private List<UploadError> uploadErrors;

    /**
     * コンストラクタ
     *
     * @param processingCount
     *            取り込み中の行番号
     */
    public UploadResult(int processingCount) {
        this.processingCount = processingCount;
    }

    /**
     * 取り込み成功総数を取得する.
     *
     * @return 取り込み成功総数
     */
    public int getSuccessTotalCount() {
        return successTotalCount;
    }

    /**
     * 取り込み成功総数を設定する.
     *
     * @param successTotalCount
     *            取り込み成功総数
     */
    public void setSuccessTotalCount(int successTotalCount) {
        this.successTotalCount = successTotalCount;
    }

    /**
     * 取り込み成功総数を「1」加算する.
     */
    public void addSuccessTotalCount() {
        successTotalCount++;
    }

    /**
     * 取り込み中の行番号を取得する.
     *
     * @return 取り込み中の行番号
     */
    public int getProcessingCount() {
        return processingCount;
    }

    /**
     * 取り込み中の行番号を設定する.
     *
     * @param processingCount
     *            取り込み中の行番号
     */
    public void setProcessingCount(int processingCount) {
        this.processingCount = processingCount;
    }

    /**
     * 取り込み中の行番号を「1」加算する.
     */
    public void addProcessingCount() {
        processingCount++;
    }

    /**
     * 取り込みエラーの情報を保持するクラスを作成する.
     *
     * @param fieldName
     *            項目名
     * @param message
     *            メッセージ
     * @return 取り込みエラーの情報を保持するクラス
     */
    public UploadError createUploadError(String fieldName, String message) {
        return new UploadError(processingCount, fieldName, message);
    }

    /**
     * 取り込みエラー一覧を取得する.
     *
     * @return 取り込みエラー一覧
     */
    public List<UploadError> getUploadErrors() {
        return uploadErrors;
    }

    /**
     * 取り込みエラー一覧を設定する.
     *
     * @param uploadErrors
     *            取り込みエラー一覧
     */
    public void setUploadErrors(List<UploadError> uploadErrors) {
        this.uploadErrors = uploadErrors;
    }

    /**
     * 取り込みエラー一覧を追加する.
     *
     * @param uploadErrors
     *            取り込みエラー一覧
     */
    public void addUploadErrors(List<UploadError> uploadErrors) {
        if (this.uploadErrors == null) {
            this.uploadErrors = new ArrayList<>();
        }

        this.uploadErrors.addAll(uploadErrors);
    }
}
