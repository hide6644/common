package common.webapp.form;

import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import common.validator.constraints.MaxFileSize;
import common.validator.constraints.NotEmptyFile;

/**
 * 取り込みファイルの情報を保持するクラス.
 */
public class UploadForm {

    /** ファイル種別 */
    @Range(min = 1, max = 3)
    private Integer fileType;

    /** 取り込みファイル情報 */
    @NotEmptyFile
    @MaxFileSize(max = 2)
    private MultipartFile fileData;

    /** 取り込み結果 */
    private UploadResult uploadResult;

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
     * 取り込みファイル情報を取得する.
     *
     * @return 取り込みファイル情報
     */
    public MultipartFile getFileData() {
        return fileData;
    }

    /**
     * 取り込みファイル情報を設定する.
     *
     * @param fileData
     *            取り込みファイル情報
     */
    public void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }

    /**
     * 取り込み結果を取得する.
     *
     * @return 取り込み結果
     */
    public UploadResult getUploadResult() {
        return uploadResult;
    }

    /**
     * 取り込み結果を設定する.
     *
     * @param uploadResult
     *            取り込み結果
     */
    public void setUploadResult(UploadResult uploadResult) {
        this.uploadResult = uploadResult;
    }
}
