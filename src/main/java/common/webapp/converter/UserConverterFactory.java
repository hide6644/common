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
    public static FileConverterStrategy<User> createConverter(FileType fileType) {
        switch (fileType) {
        case XML:
            return new XmlFileConverter<>(Users.class);
        case EXCEL:
            return new XlsFileConverter<>(Users.class);
        case CSV:
            return new CsvFileConverter<>(User.class);
        default:
            throw new FileException("errors.fileType");
        }
    }
}
