package common.webapp.converter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import common.Constants;
import common.exception.FileException;
import common.webapp.converter.util.CsvFileReader;

/**
 * CSVファイル変換処理の実装クラス.
 */
public class CsvFileConverter implements FileConverterStrategy {

    /** 保持クラス */
    private Class<?> clazz;

    /** ヘッダー情報 */
    private String[] header;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> convert(MultipartFile multipartFile) {
        CSVReader reader = null;

        try {
            List<Object> beanList = new ArrayList<Object>();
            CsvFileReader csvFileReader = new CsvFileReader(header);
            reader = new CSVReader(new InputStreamReader(multipartFile.getInputStream(), Constants.ENCODING), ',', '"', 1);

            for (String[] line : reader.readAll()) {
                Object object = csvFileReader.read(clazz.newInstance(), line);
                beanList.add(object);
            }

            return beanList;
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (IllegalArgumentException e) {
            throw new FileException("errors.convert", e);
        } catch (IndexOutOfBoundsException e) {
            throw new FileException("errors.convert", e);
        } catch (InstantiationException e) {
            throw new FileException("errors.convert", e);
        } catch (IllegalAccessException e) {
            throw new FileException("errors.convert", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }
}
