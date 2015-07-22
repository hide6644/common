package common.webapp.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.XLSDataReadException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import common.exception.FileException;
import common.webapp.converter.util.JxlsFileReader;

/**
 * XLSファイル変換処理の実装クラス.
 */
public class XlsFileConverter implements FileConverterStrategy {

    /** 保持クラス */
    private Class<?> clazz;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> convert(MultipartFile multipartFile) {
        String modelName = clazz.getSimpleName();
        Map<String, List<Object>> model = new HashMap<String, List<Object>>();
        model.put(modelName, new ArrayList<Object>());

        try {
            if (new JxlsFileReader().read(new ClassPathResource(modelName + ".xml", getClass()), multipartFile, model).isStatusOK()) {
                return model.get(modelName);
            } else {
                throw new FileException("errors.convert");
            }
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (IllegalArgumentException e) {
            throw new FileException("errors.convert", e);
        } catch (InvalidFormatException e) {
            throw new FileException("errors.convert", e);
        } catch (XLSDataReadException e) {
            throw new FileException("errors.convert", e);
        } catch (SAXException e) {
            throw new FileException("errors.convert", e);
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
}
