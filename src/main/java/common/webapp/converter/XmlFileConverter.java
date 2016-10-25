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
public class XmlFileConverter<T> implements FileConverterStrategy<T> {

    /** ファイルタイプ(XML) */
    public static final String FILE_TYPE = "1";

    /** 保持クラス */
    private Class<?> clazz;

    /**
     * コンストラクタ.
     *
     * @param clazz
     *            保持クラス
     */
    public XmlFileConverter(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> convert(MultipartFile multipartFile) {
        try {
            return ((BaseObjects<T>) new XmlFileReader().read(JAXBContext.newInstance(clazz), multipartFile)).getObjects();
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (JAXBException e) {
            throw new FileException("errors.convert", e);
        }
    }
}
