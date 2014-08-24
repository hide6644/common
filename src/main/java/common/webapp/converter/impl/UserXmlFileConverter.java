package common.webapp.converter.impl;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.web.multipart.MultipartFile;

import common.exception.FileException;
import common.model.User;
import common.webapp.converter.FileConverterStrategy;
import common.webapp.converter.util.XmlFileReader;

/**
 * ユーザXMLファイル変換処理実装クラス.
 *
 * @author hide6644
 */
public class UserXmlFileConverter implements FileConverterStrategy<User> {

    /*
     * (非 Javadoc)
     *
     * @see common.webapp.converter.FileConverterStrategy#convert(org.springframework.web.multipart.MultipartFile)
     */
    public List<User> convert(MultipartFile multipartFile) {
        try {
            @SuppressWarnings("unchecked")
            List<User> userList = (List<User>) new XmlFileReader().read(JAXBContext.newInstance(User.class), multipartFile);

            for (User user : userList) {
                user.setConfirmPassword(user.getPassword());
            }

            return userList;
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (JAXBException e) {
            throw new FileException("errors.convert", e);
        }
    }
}
