package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import common.model.User;
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
    @RequestMapping(value = "/requestRecoveryToken*", method = RequestMethod.GET)
    public String requestRecoveryToken(@RequestParam(value = "username", required = true) String username) {
        try {
            userManager.recoveryPassword(username);
        } catch (final UsernameNotFoundException ignored) {
            // lets ignore this
        }

        saveFlashMessage(getText("updatePasswordForm.recoveryToken.sent"));
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
    @RequestMapping(value = "/updatePassword*", method = RequestMethod.GET)
    public ModelAndView showForm(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) {
        if (StringUtils.isBlank(username)) {
            username = request.getRemoteUser();
        }

        if (StringUtils.isNotBlank(token) && !userManager.isRecoveryTokenValid(username, token)) {
            saveFlashError(getText("updatePasswordForm.invalidToken"));
            return new ModelAndView("redirect:/login");
        }

        return new ModelAndView("password").addObject("username", username).addObject("token", token);
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
    @RequestMapping(value = "/updatePassword*", method = RequestMethod.POST)
    public ModelAndView onSubmit(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "currentPassword", required = false) String currentPassword, @RequestParam(value = "password", required = true) String password, HttpServletRequest request) {
        if (StringUtils.isEmpty(password)) {
            saveError(getText("errors.required", getText("updatePasswordForm.newPassword")));
            return showForm(username, null, request);
        }

        User user = null;
        boolean usingToken = StringUtils.isNotBlank(token);

        if (usingToken) {
            // パスワード忘れの案内からパスワードを変更する場合
            user = userManager.updatePassword(username, null, token, password);
        } else {
            // ログイン中のユーザが自身のパスワード変更する場合
            if (!username.equals(request.getRemoteUser())) {
                throw new AccessDeniedException("You do not have permission to modify other users password.");
            }

            user = userManager.updatePassword(username, currentPassword, null, password);
        }

        if (user == null) {
            // ユーザが存在しない場合
            return errorReturnView(username, usingToken, request);
        } else {
            saveFlashMessage(getText("updated"));
            return successReturnView(usingToken, request);
        }
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
    private ModelAndView errorReturnView(String username, boolean usingToken, HttpServletRequest request) {
        if (usingToken) {
            // パスワード忘れの案内からパスワードを変更した場合
            saveFlashError(getText("updatePasswordForm.invalidToken"));
            return new ModelAndView("redirect:/login");
        } else {
            // ログイン中のユーザが自身のパスワード変更した場合
            saveError(getText("updatePasswordForm.invalidPassword"));
            return showForm(username, null, request);
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
    private ModelAndView successReturnView(boolean usingToken, HttpServletRequest request) {
        if (usingToken) {
            // パスワード忘れの案内からパスワードを変更した場合
            return new ModelAndView("redirect:/login");
        } else {
            // ログイン中のユーザが自身のパスワード変更した場合
            if (StringUtils.equals(request.getParameter("from"), "list")) {
                // ユーザ一覧から遷移した場合
                return new ModelAndView("redirect:/user?from=list");
            } else {
                return new ModelAndView("redirect:/user");
            }
        }
    }
}
