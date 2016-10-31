package common.webapp.converter;

import common.exception.FileException;
import common.model.User;
import common.model.Users;

/**
 * Userファイル変換処理クラスのインスタンスを生成する.
 */
public class UserConverterFactory {

    /**
     * プライベート・コンストラクタ.
     */
    private UserConverterFactory() {
    }

    /**
     * ファイルタイプに合わせた、ファイル変換処理クラスのインスタンスを返却する.
     *
     * @param fileType
     *            ファイルタイプ
     * @return ファイル変換処理クラスのインスタンス
     */
    public static FileConverterStrategy<User> createConverter(String fileType) {
        FileConverterStrategy<User> converter = null;

        if (fileType != null && fileType.equals(XmlFileConverter.FILE_TYPE)) {
            converter = new XmlFileConverter<>(Users.class);
        } else if (fileType != null && fileType.equals(XlsFileConverter.FILE_TYPE)) {
            converter = new XlsFileConverter<>(Users.class);
        } else if (fileType != null && fileType.equals(CsvFileConverter.FILE_TYPE)) {
            converter = new CsvFileConverter<>(User.class);
        } else {
            throw new FileException("errors.fileType");
        }

        return converter;
    }
}
