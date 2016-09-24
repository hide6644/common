package common.webapp.converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import common.Constants;
import common.exception.FileException;
import common.webapp.converter.util.CsvFileReader;

/**
 * CSVファイル変換処理の実装クラス.
 */
public class CsvFileConverter implements FileConverterStrategy {

    /** ファイルタイプ(CSV) */
    public static final String FILE_TYPE = "3";

    /** 保持クラス */
    private Class<?> clazz;

    /**
     * コンストラクタ.
     *
     * @param clazz
     *            保持クラス
     */
    public CsvFileConverter(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> convert(MultipartFile multipartFile) {
        CSVReader reader = null;

        try {
            reader = new CSVReader(new InputStreamReader(multipartFile.getInputStream(), Constants.ENCODING), ',', '"', 1);

            return reader.readAll().stream().map(line -> {
                try {
                    CsvFileReader csvFileReader = new CsvFileReader(FileUtils.readFileToString(new ClassPathResource(clazz.getSimpleName() + ".csv", getClass()).getFile(), Constants.ENCODING).split(","));
                    return csvFileReader.read(clazz.newInstance(), line);
                } catch (IOException e) {
                    throw new FileException("errors.io", e);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new FileException("errors.convert", e);
                }
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (IllegalArgumentException e) {
            throw new FileException("errors.convert", e);
        } catch (IndexOutOfBoundsException e) {
            throw new FileException("errors.convert", e);
        } catch (TypeMismatchException e) {
            throw new FileException("errors.convert", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
