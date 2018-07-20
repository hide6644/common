package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import common.dto.PasswordForm;
import common.service.UserManager;

/**
 * パスワード変更処理クラス.
 */
@Controller
public class UpdatePasswordController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * パスワード回復用のURLをメールで送信する.
     *
     * @param username
     *            ユーザ名
     * @return 遷移先
     */
    @GetMapping("/requestRecoveryToken")
    public String requestRecoveryToken(@RequestParam("username") String username) {
        try {
            userManager.recoveryPassword(username);
        } catch (final UsernameNotFoundException ignored) {
            // lets ignore this
        }

        saveFlashMessage(getText("passwordForm.recoveryToken.sent"));
        return "redirect:/login";
    }

    /**
     * パスワード変更画面初期処理.
     *
     * @param username
     *            ユーザ名
     * @param token
     *            トークン
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先画面設定
     */
    @GetMapping("/updatePassword")
    public ModelAndView showForm(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) {
        if (StringUtils.isBlank(username)) {
            username = request.getRemoteUser();
        }

        if (StringUtils.isNotBlank(token) && !userManager.isRecoveryTokenValid(username, token)) {
            saveFlashError(getText("passwordForm.invalidToken"));
            return new ModelAndView("redirect:/login");
        }

        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername(username);
        passwordForm.setToken(token);

        return new ModelAndView("password").addObject("passwordForm", passwordForm);
    }

    /**
     * パスワード変更画面保存処理.
     *
     * @param username
     *            ユーザ名
     * @param token
     *            トークン
     * @param currentPassword
     *            現在のパスワード
     * @param password
     *            パスワード
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先画面設定
     */
    @PostMapping("/updatePassword")
    public String onSubmit(@Valid PasswordForm passwordForm, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "password";
        }

        boolean usingToken = StringUtils.isNotBlank(passwordForm.getToken());

        try {
            if (usingToken) {
                // パスワード忘れの案内からパスワードを変更する場合
                userManager.updatePassword(passwordForm);
            } else {
                // ログイン中のユーザが自身のパスワード変更する場合
                if (!passwordForm.getUsername().equals(request.getRemoteUser())) {
                    throw new AccessDeniedException("You do not have permission to modify other users password.");
                }

                userManager.updatePassword(passwordForm);
            }
        } catch (UsernameNotFoundException e) {
            // ユーザが存在しない場合
            return errorReturnView(usingToken);
        }

        saveFlashMessage(getText("updated"));
        return successReturnView(usingToken, request);
    }

    /**
     * パスワード変更失敗の場合の遷移先を取得する.
     *
     * @param username
     *            ユーザ名
     * @param usingToken
     *            true:トークン有り、false:トークン無し
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先画面設定
     */
    private String errorReturnView(boolean usingToken) {
        if (usingToken) {
            // パスワード忘れの案内からパスワードを変更した場合
            saveFlashError(getText("passwordForm.invalidToken"));
            return "redirect:/login";
        } else {
            // ログイン中のユーザが自身のパスワード変更した場合
            saveError(getText("passwordForm.invalidPassword"));
            return "password";
        }
    }

    /**
     * パスワード変更成功の場合の遷移先を取得する.
     *
     * @param usingToken
     *            true:トークン有り、false:トークン無し
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先画面設定
     */
    private String successReturnView(boolean usingToken, HttpServletRequest request) {
        if (usingToken) {
            // パスワード忘れの案内からパスワードを変更した場合
            return "redirect:/login";
        } else {
            // ログイン中のユーザが自身のパスワード変更した場合
            if (StringUtils.equals(request.getParameter("from"), "list")) {
                // ユーザ一覧から遷移した場合
                return "redirect:/user?from=list";
            } else {
                return "redirect:/user";
            }
        }
    }
}
