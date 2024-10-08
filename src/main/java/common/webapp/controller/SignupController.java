package common.webapp.controller;

import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import common.dto.SignupUserForm;
import common.exception.DatabaseException;
import common.service.UserManager;
import lombok.extern.log4j.Log4j2;

/**
 * ユーザ登録処理クラス.
 */
@Controller
@Log4j2
public class SignupController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * ユーザ登録画面初期処理.
     *
     * @return ユーザ
     */
    @GetMapping("signup")
    @ModelAttribute("user")
    public SignupUserForm showForm() {
        return new SignupUserForm();
    }

    /**
     * ユーザ登録処理.
     *
     * @param signupUser
     *            新規登録ユーザ情報を
     * @param result
     *            エラーチェック結果
     * @return 遷移先
     */
    @PostMapping("signup")
    public String onSubmit(@ModelAttribute("user") @Valid SignupUserForm signupUser, BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }

        try {
            userManager.saveSignupUser(signupUser);
            saveFlashMessage(getText("signupForm.provisional.message"));
        } catch (DatabaseException e) {
            log.error(e);
            return "signup";
        }

        return "redirect:/login";
    }

    /**
     * ユーザ登録完了処理
     *
     * @param username
     *            ユーザ名
     * @param token
     *            ユーザ認証用トークン
     * @return 遷移先
     */
    @GetMapping("signupComplete")
    public String complete(@RequestParam("username") String username, @RequestParam("token") String token) {
        try {
            if (StringUtils.isNotBlank(token) && !userManager.isRecoveryTokenValid(username, token)) {
                saveFlashError(getText("signupForm.invalidToken"));
                return "redirect:/login";
            }

            userManager.enableUser(username);
            saveFlashMessage(getText("signupForm.complete.message"));
        } catch (DatabaseException e) {
            log.error(e);
            return "redirect:/login";
        }

        return "redirect:/top";
    }
}
