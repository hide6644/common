package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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
import common.service.RoleManager;
import common.service.UserManager;
import common.webapp.util.RequestUtil;

/**
 * ユーザ登録処理クラス.
 */
@Controller
public class SignupController extends BaseController {

    public static final String SIGNUP_TEMPLATE = "/signupComplete?username={username}&token={token}";

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

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
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String onSubmit(@Valid User user, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "signup";
        }

        user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
        // 新規登録時は権限を一般で設定する
        user.getRoles().clear();
        user.addRole(roleManager.getRole(Constants.USER_ROLE));

        try {
            userManager.saveUser(user);
            saveFlashMessage(getText("signupForm.provisional.message"));
        } catch (DBException e) {
            rejectValue(result, e);

            return "signup";
        }

        // 登録完了メールを送信する
        userManager.sendSignupUserEmail(user, RequestUtil.getAppURL(request) + SIGNUP_TEMPLATE);

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
            User user = userManager.getUserByUsername(username);

            if (StringUtils.isNotBlank(token) && !userManager.isRecoveryTokenValid(username, token)) {
                saveFlashError(getText("signupForm.invalidToken"));
                return "redirect:/login";
            }

            // 登録した"username"、"password"でログイン処理を行う
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
            auth.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(auth);

            user.setConfirmPassword(user.getPassword());
            user.setEnabled(true);

            userManager.saveUser(user);
            saveFlashMessage(getText("signupForm.complete.message"));
        } catch (DBException e) {
            log.error(e);

            return "redirect:/login";
        }

        return "redirect:/top";
    }
}
