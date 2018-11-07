package common.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ログイン処理クラス.
 */
@Controller
public class LoginController extends BaseController {

    /**
     * ログイン画面初期処理.
     *
     * @param model
     *            {@link ModelMap}
     * @return 遷移先
     */
    @GetMapping("login")
    public String setupLogin(ModelMap model) {
        model.addAttribute("username", "");
        model.addAttribute("password", "");
        return "login";
    }

    /**
     * ログイン画面処理.
     *
     * @param rememberMeFlg
     *            ログイン情報を保持するか
     * @param username
     *            ユーザID
     * @param password
     *            パスワード
     * @return 遷移先
     */
    @PostMapping("login")
    public String login(@RequestParam(value = "remember-me", required = false) boolean rememberMeFlg,
            @RequestParam("username") String username, @RequestParam("password") String password) {
        String forwardString = "forward:/login?username=" + username + "&password=" + password;

        if (rememberMeFlg) {
            forwardString += "&remember-me=true";
        }

        return forwardString;
    }

    /**
     * アカウント無効画面処理.
     *
     * @return 遷移先
     */
    @GetMapping("login/accountDisabled")
    public String accountDisabled() {
        saveFlashError(getText("loginForm.accountDisabled"));
        return "redirect:/login";
    }

    /**
     * アカウント停止画面処理.
     *
     * @return 遷移先
     */
    @GetMapping("login/accountLocked")
    public String accountLocked() {
        saveFlashError(getText("loginForm.accountLocked"));
        return "redirect:/login";
    }

    /**
     * アカウント有効期限切れ画面処理.
     *
     * @return 遷移先
     */
    @GetMapping("login/accountExpired")
    public String accountExpired() {
        saveFlashError(getText("loginForm.accountExpired"));
        return "redirect:/login";
    }

    /**
     * パスワード有効期限切れ画面初期処理.
     *
     * @return 遷移先
     */
    @GetMapping("login/credentialsExpired")
    public String credentialsExpired() {
        saveFlashError(getText("loginForm.credentialsExpired"));
        return "redirect:/login";
    }

    /**
     * 認証失敗画面処理.
     *
     * @return 遷移先
     */
    @GetMapping("login/badCredentials")
    public String badCredentials() {
        saveFlashError(getText("loginForm.badCredentials"));
        return "redirect:/login";
    }
}
