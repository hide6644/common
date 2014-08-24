package common.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import common.Constants;
import common.exception.DBCheckException;
import common.exception.DBException;
import common.exception.FileException;
import common.model.PaginatedList;
import common.model.User;
import common.service.RoleManager;
import common.service.UserManager;
import common.webapp.converter.FileConverterStrategy;
import common.webapp.converter.impl.UserCsvFileConverter;
import common.webapp.converter.impl.UserXlsFileConverter;
import common.webapp.converter.impl.UserXmlFileConverter;
import common.webapp.form.UploadForm;

/**
 * ユーザ画面処理クラス.
 *
 * @author hide6644
 */
@Controller
@SessionAttributes("searchUser")
public class UserListController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /** Role系処理クラス */
    @Autowired
    private RoleManager roleManager;

    /*
     * (非 Javadoc)
     *
     * @see
     * common.web.controller.BaseController#initBinder(org.springframework.web.bind.WebDataBinder)
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(Constants.DATE_TIME_FORMAT), true));
    }

    /**
     * 検索条件初期化
     *
     * @return 画面入力値保持モデル
     */
    @ModelAttribute("searchUser")
    public User getSearchUser() {
        return new User();
    }

    /**
     * ユーザ一覧表示画面処理.
     *
     * @param user
     *            画面入力値保持モデル
     * @param page
     *            表示ページ数
     * @param model
     *            画面汎用値保持モデル
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "admin/master/users/", method = RequestMethod.GET)
    public String setupList(@ModelAttribute("searchUser") User user, @RequestParam(value = "page", required = false) Integer page, Model model) {
        PaginatedList<User> paginatedList = new PaginatedList<User>(page);
        paginatedList.setSearchCondition(user);
        paginatedList.setCurrentPage(userManager.getPaged(paginatedList));

        model.addAttribute("paginatedList", paginatedList);

        return "admin/master/users/list";
    }

    /**
     * ユーザ一覧検索CSV出力処理.
     *
     * @param model
     *            画面汎用値保持モデル
     * @return ユーザ一覧
     */
    @RequestMapping(value = "admin/master/users.csv", method = RequestMethod.GET)
    public String getCsvList(Model model) {
        Set<Formatter<?>> formatters = new HashSet<Formatter<?>>();
        formatters.add(new DateFormatter(Constants.DATE_FORMAT));

        model.addAttribute("formatters", formatters);
        model.addAttribute("csv", userManager.getUsers());
        return "admin/master/users/csv/list";
    }

    /**
     * ユーザ一覧検索XLS出力処理.
     *
     * @param model
     *            画面汎用値保持モデル
     * @return ユーザ一覧
     */
    @RequestMapping(value = "admin/master/users.xls", method = RequestMethod.GET)
    public String getXlsList(Model model) {
        model.addAttribute("cols", roleManager.getAll());
        model.addAttribute("users", userManager.getUsers());
        return "admin/master/users/jxls/list";
    }

    /**
     * ユーザ一覧検索XML出力処理.
     *
     * @return ユーザ一覧
     */
    @RequestMapping(value = "admin/master/users.xml", method = RequestMethod.GET)
    public @ResponseBody
    List<User> getXmlList() {
        return userManager.getUsers();
    }

    /**
     * ユーザ取込画面初期処理.
     *
     * @param model
     *            画面汎用値保持モデル
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "admin/master/users/upload", method = RequestMethod.GET)
    public String setupUpload(Model model) {
        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileConverterStrategy.FILE_TYPE_XML);
        model.addAttribute("uploadForm", uploadForm);

        return "admin/master/users/upload";
    }

    /**
     * ユーザ取込画面処理.
     *
     * @param uploadForm
     *            取込ファイル保持モデル
     * @param result
     *            エラーチェック結果
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "admin/master/users/upload", method = RequestMethod.POST)
    public String upload(@Valid UploadForm uploadForm, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/master/users/upload";
        }

        int insertedRowCount = 0;
        int errorRowCount = 0;
        List<String> errors = new ArrayList<String>();

        try {
            FileConverterStrategy<User> converter = null;

            if (uploadForm.getFileType().equals(FileConverterStrategy.FILE_TYPE_XML)) {
                converter = new UserXmlFileConverter();
            } else if (uploadForm.getFileType().equals(FileConverterStrategy.FILE_TYPE_XLS)) {
                converter = new UserXlsFileConverter();
            } else if (uploadForm.getFileType().equals(FileConverterStrategy.FILE_TYPE_CSV)) {
                converter = new UserCsvFileConverter();
            } else {
                throw new FileException("errors.fileType");
            }

            for (User user : converter.convert(uploadForm.getFileData())) {
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

            return "admin/master/users/upload";
        }

        if (insertedRowCount > 0) {
            saveMessage(getText("inserted", String.valueOf(insertedRowCount)));
        }

        if (errorRowCount > 0) {
            save("errors_upload_list", errors);

            return "admin/master/users/upload";
        }

        return "redirect:./";
    }
}
