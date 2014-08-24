package common.webapp.converter.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;

import common.Constants;
import common.exception.FileException;
import common.model.User;
import common.webapp.converter.FileConverterStrategy;
import common.webapp.converter.util.CsvFileReader;

/**
 * ユーザCSVファイル変換処理実装クラス.
 *
 * @author hide6644
 */
public class UserCsvFileConverter implements FileConverterStrategy<User> {

    /** ヘッダー行 */
    private int header = 1;

    /*
     * (非 Javadoc)
     *
     * @see common.webapp.converter.FileConverterStrategy#convert(org.springframework.web.multipart.MultipartFile)
     */
    public List<User> convert(MultipartFile multipartFile) {
        CSVReader reader = null;
        CsvFileReader csvFileReader = new CsvFileReader("username", "password", "firstName", "lastName", "email", "enabled", "accountLocked", "accountExpiredDate", "credentialsExpiredDate", "roles.code", "roles.name");
        csvFileReader.getBeanWrapper().registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(Constants.DATE_FORMAT), true));

        try {
            List<User> beanList = new ArrayList<User>();
            reader = new CSVReader(new InputStreamReader(multipartFile.getInputStream(), Constants.ENCODING), ',', '"', header);

            for (String[] line : reader.readAll()) {
                User user = new User();
                user = (User) csvFileReader.read(user, line);
                user.setConfirmPassword(user.getPassword());

                beanList.add(user);
            }

            return beanList;
        } catch (IOException e) {
            throw new FileException("errors.io", e);
        } catch (IllegalArgumentException e) {
            throw new FileException("errors.convert", e);
        } catch (IndexOutOfBoundsException e) {
            throw new FileException("errors.convert", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
