package common.webapp.form;

import java.util.ArrayList;
import java.util.List;

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
    List<UploadError> uploadErrors;

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
