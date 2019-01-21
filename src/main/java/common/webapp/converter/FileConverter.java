package common.webapp.converter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * ファイル変換処理インターフェイス.
 */
@FunctionalInterface
public interface FileConverter<T> {

    /**
     * アップロードファイルを保持クラス一覧に変換する.
     *
     * @param multipartFile
     *            アップロードファイル
     * @return 保持クラス一覧
     */
    List<T> convert(MultipartFile multipartFile);
}
