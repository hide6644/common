package common.webapp.converter;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.web.multipart.MultipartFile;

import common.exception.FileException;
import common.model.BaseObjects;
import common.webapp.converter.util.XmlFileReader;

/**
 * XMLファイル変換処理の実装クラス.
 */
public class XmlFileConverter implements FileConverterStrategy {

    /** 保持クラス */
    private Class<?> clazz;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> convert(MultipartFile multipartFile) {
        try {
            return ((BaseObjects<?>) new XmlFileReader().read(JAXBContext.newInstance(clazz), multipartFile)).getObjects();
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (JAXBException e) {
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
