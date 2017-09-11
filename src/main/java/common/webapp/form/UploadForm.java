package common.webapp.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import common.validator.constraints.MaxFileSize;
import common.validator.constraints.NotEmptyFile;

/**
 * アップロードファイルの情報を保持するクラス.
 */
public class UploadForm {

    /** ファイル種別 */
    @Range(min = 1, max = 3)
    private Integer fileType;

    /** アップロードファイル情報 */
    @NotEmptyFile
    @MaxFileSize(max = 2)
    private MultipartFile fileData;

    /** 件数 */
    private int count;

    /** 取り込みエラー一覧 */
    Map<Integer, List<String>> errors;

    /**
     * ファイル種別を取得する.
     *
     * @return ファイル種別
     */
    public Integer getFileType() {
        return fileType;
    }

    /**
     * ファイル種別を設定する.
     *
     * @param fileType
     *            ファイル種別
     */
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    /**
     * アップロードファイル情報を取得する.
     *
     * @return アップロードファイル情報
     */
    public MultipartFile getFileData() {
        return fileData;
    }

    /**
     * アップロードファイル情報を設定する.
     *
     * @param fileData
     *            アップロードファイル情報
     */
    public void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }

    /**
     * 件数を取得する.
     *
     * @return 件数
     */
    public int getCount() {
        return count;
    }

    /**
     * 件数を設定する.
     *
     * @param count
     *            件数
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 取り込みエラー一覧を取得する.
     *
     * @return 取り込みエラー一覧
     */
    public Map<Integer, List<String>> getErrors() {
        return errors;
    }

    /**
     * 取り込みエラー一覧を設定する.
     *
     * @param errors
     *            取り込みエラー一覧
     */
    public void setErrors(Map<Integer, List<String>> errors) {
        this.errors = errors;
    }

    /**
     * 取り込みエラーを追加する.
     *
     * @param rowNo
     *            行番号
     * @param errorMessage
     *            エラーメッセージ
     */
    public void addErrorMessage(Integer rowNo, List<String> errorMessage) {
        if (errors == null) {
            errors = new HashMap<>();
        }

        errors.put(rowNo, errorMessage);
    }
}
