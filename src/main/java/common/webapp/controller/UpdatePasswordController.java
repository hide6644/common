package common.webapp.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * @param model
     *            {@link Model}
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先
     */
    @GetMapping("/updatePassword")
    public String showForm(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "token", required = false) String token, Model model, HttpServletRequest request) {
        if (StringUtils.isBlank(username)) {
            username = request.getRemoteUser();
        }

        if (token != null && !userManager.isRecoveryTokenValid(username, token)) {
            saveFlashError(getText("passwordForm.invalidToken"));
            return "redirect:/login";
        }

        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername(username);
        passwordForm.setToken(token);
        model.addAttribute("passwordForm", passwordForm);

        return "password";
    }

    /**
     * パスワード変更画面保存処理.
     *
     * @param passwordForm
     *            パスワード情報
     * @param result
     *            エラーチェック結果
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先
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
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            // 引き数が正しくない、ユーザが存在しない場合
            return errorReturnView(usingToken);
        }

        saveFlashMessage(getText("updated"));
        return successReturnView(usingToken, Objects.equals(request.getParameter("from"), "list"));
    }

    /**
     * パスワード変更失敗の場合の遷移先を取得する.
     *
     * @param usingToken
     *            true:トークン有り、false:トークン無し
     * @return 遷移先
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
     * @param fromList
     *            true:ユーザ一覧から遷移、false:ユーザ一覧以外の画面から遷移
     * @return 遷移先
     */
    private String successReturnView(boolean usingToken, boolean fromList) {
        if (usingToken) {
            // パスワード忘れの案内からパスワードを変更した場合
            return "redirect:/login";
        } else {
            // ログイン中のユーザが自身のパスワード変更した場合
            if (fromList) {
                // ユーザ一覧から遷移した場合
                return "redirect:/user?from=list";
            } else {
                return "redirect:/user";
            }
        }
    }
}
