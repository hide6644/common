package common.webapp.converter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * ファイル変換処理インターフェイス.
 */
public interface FileConverterStrategy<T> {

    /**
     * アップロードファイルを保持クラス一覧に変換する.
     *
     * @param multipartFile
     *            アップロードファイル
     * @return 保持クラス一覧
     */
    public abstract List<T> convert(MultipartFile multipartFile);
}
