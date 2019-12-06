package common.webapp.converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.XLSDataReadException;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import common.Constants;
import common.entity.BaseObjects;
import common.entity.User;
import common.entity.Users;
import common.exception.FileException;
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
     * ファイルタイプに合わせた、ファイル変換処理を生成する.
     *
     * @param fileType
     *            ファイルタイプ
     * @return ファイル変換処理クラスの関数
     */
    public static FileConverter<User> createConverter(FileType fileType) {
        switch (fileType) {
        case XML:
            return createXmlConverter();
        case EXCEL:
            return createExcelConverter();
        case CSV:
            return createCsvConverter();
        default:
            throw new FileException("errors.fileType");
        }
    }

    /**
     * XMLファイル変換処理を生成する.
     *
     * @return XMLファイル変換処理クラスの関数
     */
    @SuppressWarnings("unchecked")
    private static FileConverter<User> createXmlConverter() {
        return multipartFile -> {
            try {
                return ((BaseObjects<User>) new XmlFileReader().read(JAXBContext.newInstance(Users.class), multipartFile)).getObjects();
            } catch (IOException e) {
                throw new FileException("errors.io", e);
            } catch (JAXBException e) {
                throw new FileException("errors.convert", e);
            }
        };
    }

    /**
     * EXCELファイル変換処理を生成する.
     *
     * @return EXCELファイル変換処理クラスの関数
     */
    private static FileConverter<User> createExcelConverter() {
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
    }

    /**
     * CSVファイル変換処理を生成する.
     *
     * @return CSVファイル変換処理クラスの関数
     */
    private static FileConverter<User> createCsvConverter() {
        return multipartFile -> {
            try (InputStreamReader is = new InputStreamReader(multipartFile.getInputStream(), Constants.ENCODING);
                    CSVReader reader = new CSVReaderBuilder(is).withSkipLines(1).build()) {
                ColumnPositionMappingStrategy<User> strat = new ColumnPositionMappingStrategy<>();
                strat.setType(User.class);
                strat.setColumnMapping(FileUtils.readFileToString(new ClassPathResource("/common/webapp/converter/User.csv").getFile(), Constants.ENCODING).split(","));

                CsvToBean<User> csv = new CsvToBean<>();
                csv.setCsvReader(reader);
                csv.setMappingStrategy(strat);
                return csv.parse();
            } catch (IOException e) {
                throw new FileException("errors.io", e);
            } catch (IllegalStateException e) {
                throw new FileException("errors.convert", e);
            }
        };
    }
}
