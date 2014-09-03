package common.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import common.webapp.util.RequestUtil;


/**
 * パスワード変更処理クラス.
 *
 * @author hide6644
 */
@Controller
public class UpdatePasswordController extends BaseController {

    public static final String RECOVERY_PASSWORD_TEMPLATE = "/updatePassword?username={username}&token={token}";

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "/requestRecoveryToken*", method = RequestMethod.GET)
    public String requestRecoveryToken(@RequestParam(value = "username", required = true) final String username, final HttpServletRequest request) {
        try {
            userManager.sendPasswordRecoveryEmail(username, RequestUtil.getAppURL(request) + RECOVERY_PASSWORD_TEMPLATE);
        } catch (final UsernameNotFoundException ignored) {
            // lets ignore this
        }

        saveFlashMessage(getText("updatePasswordForm.recoveryToken.sent"));
        return "redirect:/";
    }

    @RequestMapping(value = "/updatePassword*", method = RequestMethod.GET)
    public ModelAndView showForm(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) {
        if (StringUtils.isBlank(username)) {
            username = request.getRemoteUser();
        }

        if (StringUtils.isNotBlank(token) && !userManager.isRecoveryTokenValid(username, token)) {
            saveFlashError(getText("updatePasswordForm.invalidToken"));
            return new ModelAndView("redirect:/");
        }

        return new ModelAndView("updatePassword").addObject("username", username).addObject("token", token);
    }

    @RequestMapping(value = "/updatePassword*", method = RequestMethod.POST)
    public ModelAndView onSubmit(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "currentPassword", required = false) String currentPassword, @RequestParam(value = "password", required = true) String password, HttpServletRequest request)
            throws Exception {
        if (StringUtils.isEmpty(password)) {
            saveError(getText("errors.required", getText("updatePasswordForm.new") + getText("user.password")));
            return showForm(username, null, request);
        }

        User user = null;
        boolean usingToken = StringUtils.isNotBlank(token);

        if (usingToken) {
            user = userManager.updatePassword(username, null, token, password, RequestUtil.getAppURL(request));
        } else {
            if (!username.equals(request.getRemoteUser())) {
                throw new AccessDeniedException("You do not have permission to modify other users password.");
            }

            user = userManager.updatePassword(username, currentPassword, null, password, RequestUtil.getAppURL(request));
        }

        if (user != null) {
            saveFlashMessage(getText("updated", 1));
        } else {
            if (usingToken) {
                saveFlashError(getText("updatePasswordForm.invalidToken"));
            } else {
                saveFlashError(getText("updatePasswordForm.invalidPassword"));
                return showForm(username, null, request);
            }
        }

        return new ModelAndView("redirect:/");
    }

}