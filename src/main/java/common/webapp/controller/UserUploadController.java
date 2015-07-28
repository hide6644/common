package common.webapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.exception.FileException;
import common.service.UserManager;
import common.webapp.converter.XmlFileConverter;
import common.webapp.form.UploadForm;

@Controller
@RequestMapping("/admin/master/uploadUsers")
public class UserUploadController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * ユーザ取込画面初期処理.
     *
     * @return 画面入力値保持モデル
     */
    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    public UploadForm setupUpload() {
        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(XmlFileConverter.FILE_TYPE);

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

        try {
            userManager.uploadUsers(uploadForm);
        } catch (FileException e) {
            saveError(e);
            return "admin/master/uploadUsers";
        }

        if (uploadForm.getCount() > 0) {
            saveFlashMessage(getText("uploaded", String.valueOf(uploadForm.getCount())));
        }

        for (Integer rowNo : uploadForm.getErrorNo()) {
            saveFlash("errors_upload_list", getText("errors.upload", String.valueOf(rowNo)));
        }

        return "redirect:/admin/master/uploadUsers";
    }
}