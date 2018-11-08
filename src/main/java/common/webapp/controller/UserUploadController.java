package common.webapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import common.dto.UploadForm;
import common.exception.FileException;
import common.service.UserManager;
import common.webapp.converter.FileType;

/**
 * ユーザ取込処理クラス.
 */
@Controller
@RequestMapping("/admin/master/uploadUsers")
public class UserUploadController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * ユーザ取込画面初期処理.
     *
     * @return 取り込みファイルの情報
     */
    @GetMapping
    public UploadForm setupUpload() {
        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.XML.getValue());
        return uploadForm;
    }

    /**
     * ユーザ取り込み処理.
     *
     * @param uploadForm
     *            取り込みファイルの情報
     * @param result
     *            エラーチェック結果
     * @return 遷移先
     */
    @PostMapping
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

        if (uploadForm.getUploadResult().getUploadErrors() != null) {
            if (uploadForm.getUploadResult().getSuccessTotalCount() > 0) {
                saveMessage(getText("uploaded", String.valueOf(uploadForm.getUploadResult().getSuccessTotalCount())));
            }

            saveError(getText("errors.upload"));
            return "admin/master/uploadUsers";
        } else {
            saveFlashMessage(getText("uploaded", String.valueOf(uploadForm.getUploadResult().getSuccessTotalCount())));
        }

        return "redirect:/admin/master/uploadUsers";
    }
}
