package common.webapp.converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.XLSDataReadException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import com.opencsv.CSVReader;

import common.Constants;
import common.exception.FileException;
import common.model.BaseObjects;
import common.model.User;
import common.model.Users;
import common.webapp.converter.util.CsvFileReader;
import common.webapp.converter.util.JxlsFileReader;
import common.webapp.converter.util.XmlFileReader;

/**
 * Userファイル変換処理クラスの関数を生成する.
 */
public class UserFileConverterFactory {

    /**
     * プライベート・コンストラクタ.
     */
    private UserFileConverterFactory() {
    }

    /**
     * ファイルタイプに合わせた、ファイル変換処理を返却する.
     *
     * @param fileType
     *            ファイルタイプ
     * @return ファイル変換処理クラスの関数
     */
    @SuppressWarnings("unchecked")
    public static FileConverter<User> createConverter(FileType fileType) {
        switch (fileType) {
        case XML:
            return multipartFile -> {
                try {
                    return ((BaseObjects<User>) new XmlFileReader().read(JAXBContext.newInstance(Users.class), multipartFile)).getObjects();
                } catch (IOException e) {
                    throw new FileException("errors.io", e);
                } catch (JAXBException e) {
                    throw new FileException("errors.convert", e);
                }
            };
        case EXCEL:
            return multipartFile -> {
                Map<String, List<User>> model = new HashMap<>();
                model.put("Users", new ArrayList<>());

                try {
                    if (new JxlsFileReader().read(new ClassPathResource("/common/webapp/converter/User.xml"), multipartFile, model).isStatusOK()) {
                        return model.get("Users");
                    } else {
                        throw new FileException("errors.convert");
                    }
                } catch (IOException e) {
                    throw new FileException("errors.io", e);
                } catch (IllegalArgumentException | InvalidFormatException | XLSDataReadException | SAXException e) {
                    throw new FileException("errors.convert", e);
                }
            };
        case CSV:
            return multipartFile -> {
                try (CSVReader reader = new CSVReader(new InputStreamReader(multipartFile.getInputStream(), Constants.ENCODING), ',', '"', 1);) {
                    return reader.readAll().stream().map(line -> {
                        try {
                            CsvFileReader csvFileReader = new CsvFileReader(
                                    FileUtils.readFileToString(new ClassPathResource("/common/webapp/converter/User.csv").getFile(), Constants.ENCODING).split(","));
                            return csvFileReader.read(new User(), line);
                        } catch (IOException e) {
                            throw new FileException("errors.io", e);
                        }
                    }).collect(Collectors.toList());
                } catch (IOException e) {
                    throw new FileException("errors.io", e);
                } catch (IllegalArgumentException | IndexOutOfBoundsException | TypeMismatchException e) {
                    throw new FileException("errors.convert", e);
                }
            };
        default:
            throw new FileException("errors.fileType");
        }
    }
}
