package common.webapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.Constants;
import common.exception.DBCheckException;
import common.exception.DBException;
import common.exception.FileException;
import common.model.Role;
import common.model.User;
import common.model.Users;
import common.service.RoleManager;
import common.service.UserManager;
import common.webapp.converter.CsvFileConverter;
import common.webapp.converter.FileConverterStrategy;
import common.webapp.converter.XlsFileConverter;
import common.webapp.converter.XmlFileConverter;
import common.webapp.form.UploadForm;

@Controller
@RequestMapping("/admin/master/uploadUsers")
public class UserUploadController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

    /**
     * ユーザ取込画面初期処理.
     *
     * @return 画面入力値保持モデル
     */
    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    public UploadForm setupUpload() {
        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileConverterStrategy.FILE_TYPE_XML);

        return uploadForm;
    }

    /**
     * ユーザ取り込み処理.
     *
     * @param uploadForm
     *            アップロードファイルの情報を保持
     * @param result
     *            エラーチェック結果
     * @return 遷移先jsp名
     */
    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@Valid UploadForm uploadForm, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/master/uploadUsers";
        }

        int insertedRowCount = 0;
        int errorRowCount = 0;
        List<String> errors = new ArrayList<String>();

        try {
            FileConverterStrategy converter = null;

            if (uploadForm.getFileType().equals(FileConverterStrategy.FILE_TYPE_XML)) {
                converter = new XmlFileConverter();
                converter.setClazz(Users.class);
            } else if (uploadForm.getFileType().equals(FileConverterStrategy.FILE_TYPE_XLS)) {
                converter = new XlsFileConverter();
                converter.setClazz(Users.class);
            } else if (uploadForm.getFileType().equals(FileConverterStrategy.FILE_TYPE_CSV)) {
                converter = new CsvFileConverter();
                converter.setClazz(User.class);
                ((CsvFileConverter) converter).setHeader(new String[]{"username", "password", "firstName", "lastName", "email"});
            } else {
                throw new FileException("errors.fileType");
            }

            @SuppressWarnings("unchecked")
            List<User> userList = (List<User>) converter.convert(uploadForm.getFileData());
            Role role =roleManager.getRole(Constants.USER_ROLE);

            for (User user : userList) {
                // 不足部分の補完
                user.setConfirmPassword(user.getPassword());
                user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
                // 新規登録時は権限を一般で設定する
                user.getRoles().clear();
                user.addRole(role);
                user.setEnabled(true);

                // エラーチェック
                Set<ConstraintViolation<User>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(user);

                if (violations.size() > 0) {
                    errors.add(getText("errors.upload", String.valueOf(insertedRowCount + errorRowCount + 1)));
                    errorRowCount++;
                } else {
                    try {
                        userManager.saveUser(user);
                        insertedRowCount++;
                    } catch (DBCheckException e) {
                        errors.add(getText("errors.upload", String.valueOf(insertedRowCount + errorRowCount + 1)));
                        errorRowCount++;
                    } catch (DBException e) {
                        errors.add(getText("errors.upload", String.valueOf(insertedRowCount + errorRowCount + 1)));
                        errorRowCount++;
                    }
                }
            }
        } catch (FileException e) {
            saveError(e);

            return "admin/master/uploadUsers";
        }

        if (insertedRowCount > 0) {
            saveFlashMessage(getText("uploaded", String.valueOf(insertedRowCount)));
        }

        if (errorRowCount > 0) {
            for (String error : errors) {
                save("errors_upload_list", error);
            }

            return "admin/master/uploadUsers";
        }

        return "redirect:/admin/master/uploadUsers";
    }
}
