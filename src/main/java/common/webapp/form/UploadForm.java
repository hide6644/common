package common.webapp.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import common.validator.constraints.MaxFileSize;
import common.validator.constraints.NotEmptyFile;

/**
 * アップロードファイルの情報を保持するクラス.
 */
public class UploadForm implements Serializable {

    /** ファイル種別 */
    @NotEmpty
    private String fileType;

    /** アップロードファイル情報 */
    @NotEmptyFile
    @MaxFileSize(max = 2)
    private MultipartFile fileData;

    /** 件数 */
    private int count;

    /** 取り込みエラーの行番号 */
    List<Integer> errorNo = new ArrayList<Integer>();

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
     * 取り込みエラーの行番号を取得する.
     *
     * @return 取り込みエラーの行番号
     */
    public List<Integer> getErrorNo() {
        return errorNo;
    }

    /**
     * 取り込みエラーの行番号を設定する.
     *
     * @param errorNo
     *            取り込みエラーの行番号
     */
    public void setErrorNo(List<Integer> errorNo) {
        this.errorNo = errorNo;
    }

    /**
     * 取り込みエラーの行番号を追加する.
     *
     * @param rowNo
     *            行番号
     */
    public void addErrorNo(Integer rowNo) {
        getErrorNo().add(rowNo);
    }
}
