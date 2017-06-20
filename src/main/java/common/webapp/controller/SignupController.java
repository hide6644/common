package common.webapp.controller;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import common.Constants;
import common.exception.DBException;
import common.model.Role;
import common.model.User;
import common.service.UserManager;

/**
 * ユーザ登録処理クラス.
 */
@Controller
public class SignupController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * ユーザ登録画面初期処理.
     *
     * @return ユーザ
     */
    @ModelAttribute
    @RequestMapping(value = "signup", method = RequestMethod.GET)
    public User showForm() {
        User user = new User();
        user.addRole(new Role(Constants.USER_ROLE));
        return user;
    }

    /**
     * ユーザ登録処理.
     *
     * @param user
     *            画面入力値保持モデル
     * @param result
     *            エラーチェック結果
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String onSubmit(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }

        try {
            userManager.saveSignupUser(user);
            saveFlashMessage(getText("signupForm.provisional.message"));
        } catch (DBException e) {
            rejectValue(result, e);

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
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "signupComplete", method = RequestMethod.GET)
    public String complete(@RequestParam("username") String username, @RequestParam("token") String token) {
        try {
            if (StringUtils.isNotBlank(token) && !userManager.isRecoveryTokenValid(username, token)) {
                saveFlashError(getText("signupForm.invalidToken"));
                return "redirect:/login";
            }

            User user = userManager.getUserByUsername(username);
            // 登録した"username"、"password"でログイン処理を行う
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
            auth.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(auth);

            userManager.enableUser(user);
            saveFlashMessage(getText("signupForm.complete.message"));
        } catch (DBException e) {
            log.error(e);

            return "redirect:/login";
        }

        return "redirect:/top";
    }
}
