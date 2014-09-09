package common.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import common.service.UserManager;

/**
 * ログイン処理クラス.
 */
@Controller
public class LoginController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * ログイン画面初期処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView setupLogin() {
        Model model = new ExtendedModelMap();
        model.addAttribute("username", "");
        model.addAttribute("password", "");

        return new ModelAndView("login", model.asMap());
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
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "_spring_security_remember_me", required = false) boolean rememberMeFlg, @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password) {
        String forwardString = "forward:/j_spring_security_check?j_username=" + username + "&j_password=" + password;

        if (rememberMeFlg) {
            forwardString += "&_spring_security_remember_me=true";
        }

        return forwardString;
    }

    /**
     * アカウント無効画面処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login/accountDisabled", method = RequestMethod.GET)
    public String accountDisabled() {
        saveFlashError(getText("loginForm.accountDisabled"));

        return "redirect:/login";
    }

    /**
     * アカウント停止画面処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login/accountLocked", method = RequestMethod.GET)
    public String accountLocked() {
        saveFlashError(getText("loginForm.accountLocked"));

        return "redirect:/login";
    }

    /**
     * アカウント有効期限切れ画面処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login/accountExpired", method = RequestMethod.GET)
    public String accountExpired() {
        saveFlashError(getText("loginForm.accountExpired"));

        return "redirect:/login";
    }

    /**
     * パスワード有効期限切れ画面初期処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login/credentialsExpired", method = RequestMethod.GET)
    public String setupCredentialsExpired() {
        saveFlashError(getText("loginForm.credentialsExpired"));

        return "redirect:/login";
    }

    /**
     * 認証失敗画面処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "login/badCredentials", method = RequestMethod.GET)
    public String badCredentials() {
        saveFlashError(getText("loginForm.badCredentials"));

        return "redirect:/login";
    }
}
