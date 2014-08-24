package common.webapp.form;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import common.validator.constraints.MaxFileSize;
import common.validator.constraints.NotEmptyFile;

/**
 * アップロードファイルの情報を保持するクラス.
 *
 * @author hide6644
 */
public class UploadForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ファイル種別 */
    @NotEmpty
    private String fileType;

    /** アップロードファイル情報 */
    @NotEmptyFile
    @MaxFileSize(max = 2)
    private CommonsMultipartFile fileData;

    /**
     * ファイル種別を取得する.
     *
     * @return ファイル種別
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * ファイル種別を設定する.
     *
     * @param fileType
     *            ファイル種別
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * アップロードファイル情報を取得する.
     *
     * @return アップロードファイル情報
     */
    public CommonsMultipartFile getFileData() {
        return fileData;
    }

    /**
     * アップロードファイル情報を設定する.
     *
     * @param fileData
     *            アップロードファイル情報
     */
    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }
}
