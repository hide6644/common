package common.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 取り込みエラーの情報を保持するクラス.
 */
@AllArgsConstructor
@Getter
public final class UploadError implements Serializable {

    /** 行番号 */
    private final int rowNo;

    /** 項目名 */
    private final String fieldName;

    /** メッセージ */
    private final String message;
}
