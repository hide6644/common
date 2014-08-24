package common.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.Constants;
import common.exception.DBException;
import common.model.Role;
import common.model.User;
import common.service.RoleManager;
import common.service.UserManager;
import common.webapp.util.RequestUtil;

/**
 * ユーザ登録情報変更画面処理クラス.
 *
 * @author hide6644
 */
@Controller
@RequestMapping("/user*")
public class UserController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

    /*
     * (非 Javadoc)
     *
     * @see common.web.controller.BaseController#initBinder(org.springframework.web.bind.WebDataBinder)
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(Constants.DATE_TIME_FORMAT), true));
    }

    @ModelAttribute("user")
    public User loadUser(HttpServletRequest request) {
        String userId = request.getParameter("id");

        if (isFormSubmission(request) && StringUtils.isNotBlank(userId)) {
            return userManager.getUser(userId);
        }

        return new User();
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    public User showForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // If not an administrator, make sure user is not trying to add or edit another user
        if (!request.isUserInRole(Constants.ADMIN_ROLE) && !isFormSubmission(request)) {
            if (isAdd(request) || request.getParameter("id") != null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                log.warn("User '" + request.getRemoteUser() + "' is trying to edit user with id '" + request.getParameter("id") + "'");
                throw new AccessDeniedException("You do not have permission to modify other users.");
            }
        }

        if (!isFormSubmission(request)) {
            String userId = request.getParameter("id");

            User user = null;
            if (userId == null && !isAdd(request)) {
                user = userManager.getUserByUsername(request.getRemoteUser());
            } else if (!StringUtils.isBlank(userId) && !"".equals(request.getParameter("version"))) {
                user = userManager.getUser(userId);
            } else {
                user = new User();
                user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
                user.addRole(new Role(Constants.USER_ROLE));
            }

            return user;
        } else {
            // populate user object from database, so all fields don't need to be hidden fields in form
            return userManager.getUser(request.getParameter("id"));
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@ModelAttribute("user") @Valid User user, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("cancel") != null) {
            if (!StringUtils.equals(request.getParameter("from"), "list")) {
                return "redirect:/top";
            } else {
                return "redirect:./";
            }
        }

        if (result.hasErrors() && request.getParameter("delete") == null) {
            return "user";
        }

        if (request.getParameter("delete") != null) {
            userManager.removeUser(user.getId().toString());
            saveFlashMessage(getText("deleted", 1));

            return "redirect:./";
        } else {
            if (request.isUserInRole(Constants.ADMIN_ROLE)) {
                String[] userRoles = request.getParameterValues("userRoles");

                if (userRoles != null) {
                    user.getRoles().clear();
                    for (String roleName : userRoles) {
                        user.addRole(roleManager.getRole(roleName));
                    }
                }
            } else {
                User cleanUser = userManager.getUserByUsername(request.getRemoteUser());
                user.setRoles(cleanUser.getRoles());
            }

            try {
                userManager.saveUser(user);
            } catch (AccessDeniedException ade) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return null;
            } catch (DBException e) {
                rejectValue(result, e);

                return "user";
            }

            if (!StringUtils.equals(request.getParameter("from"), "list")) {
                saveFlashMessage(getText("updated", 1));

                return "redirect:/top";
            } else {
                if (StringUtils.isBlank(request.getParameter("version"))) {
                    saveFlashMessage(getText("inserted", 1));

                    try {
                        String resetPasswordUrl = userManager.buildRecoveryPasswordUrl(user, UpdatePasswordController.RECOVERY_PASSWORD_TEMPLATE);
                        sendUserMessage(user, RequestUtil.getAppURL(request) + resetPasswordUrl);
                    } catch (MailException e) {
                        if (log.isWarnEnabled()) {
                            log.warn(e);
                        }

                        saveFlashError(getText("errors.send.email"));
                    }

                    return "redirect:./";
                } else {
                    saveFlashMessage(getText("updated", 1));

                    return "redirect:./";
                }
            }
        }
    }

    private void sendUserMessage(User user, String url) {
        mailMessage.setSubject("[" + getText("webapp.name") + "] " + getText("userForm.email.subject"));
        mailMessage.setTo(user.getEmail());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);

        model.put("message", getText("userForm.email.message"));
        model.put("URL", url);
        mailEngine.sendMessage(mailMessage, "accountCreated.vm", model);
    }

    private boolean isFormSubmission(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("post");
    }

    private boolean isAdd(HttpServletRequest request) {
        String method = request.getParameter("method");
        return (method != null && method.equalsIgnoreCase("add"));
    }
}
