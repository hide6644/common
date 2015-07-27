package common.webapp.converter;

import common.exception.FileException;
import common.model.User;
import common.model.Users;

public class UserConverterFactory {

    /**
     * プライベート・コンストラクタ.
     */
    private UserConverterFactory () {
    }

    public static  FileConverterStrategy createConverter(String fileType) {
        FileConverterStrategy converter = null;

        if (fileType != null && fileType.equals(XmlFileConverter.FILE_TYPE)) {
            converter = new XmlFileConverter(Users.class);
        } else if (fileType != null && fileType.equals(XlsFileConverter.FILE_TYPE)) {
            converter = new XlsFileConverter(Users.class);
        } else if (fileType != null && fileType.equals(CsvFileConverter.FILE_TYPE)) {
            converter = new CsvFileConverter(User.class);
        } else {
            throw new FileException("errors.fileType");
        }

        return converter;
    }
}
