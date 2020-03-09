package common.dto;

import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import common.validator.constraints.MaxFileSize;
import common.validator.constraints.NotEmptyFile;
import lombok.Getter;
import lombok.Setter;

/**
 * 取り込みファイルの情報を保持するクラス.
 */
@Getter
@Setter
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
}
