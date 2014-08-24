package common.webapp.converter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jxls.reader.XLSDataReadException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import common.exception.FileException;
import common.model.User;
import common.webapp.converter.FileConverterStrategy;
import common.webapp.converter.util.JxlsFileReader;

/**
 * ユーザXLSファイル変換処理実装クラス.
 *
 * @author hide6644
 */
public class UserXlsFileConverter implements FileConverterStrategy<User> {

    /** XLSファイル解析に使用するXMLファイル */
    private Resource uploadFileTemplate = new ClassPathResource("users.xml", getClass());

    /*
     * (非 Javadoc)
     *
     * @see common.webapp.converter.FileConverterStrategy#convert(org.springframework.web.multipart.MultipartFile)
     */
    public List<User> convert(MultipartFile multipartFile) {
        Map<String, List<User>> model = new HashMap<String, List<User>>();
        model.put("users", new ArrayList<User>());

        try {
            if (new JxlsFileReader().read(uploadFileTemplate, multipartFile, model).isStatusOK()) {
                return model.get("users");
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
}
