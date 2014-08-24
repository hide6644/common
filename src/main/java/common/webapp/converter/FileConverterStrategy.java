package common.webapp.converter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * ファイル変換処理インターフェイス.
 *
 * @author hide6644
 */
public interface FileConverterStrategy<T> {

    /** ファイルタイプ(XML) */
    public static final String FILE_TYPE_XML = "1";

    /** ファイルタイプ(XLS) */
    public static final String FILE_TYPE_XLS = "2";

    /** ファイルタイプ(CSV) */
    public static final String FILE_TYPE_CSV = "3";

    /**
     * アップロードファイルを保持クラス一覧に変換する.
     *
     * @param multipartFile
     *            アップロードファイル
     * @return 保持クラス一覧
     */
    public abstract List<T> convert(MultipartFile multipartFile);
}
