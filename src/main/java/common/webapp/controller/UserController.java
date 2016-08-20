package common.webapp.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.Constants;
import common.exception.DBException;
import common.model.Role;
import common.model.User;
import common.service.UserManager;
import common.webapp.util.RequestUtil;

/**
 * ユーザ登録情報変更処理クラス.
 */
@Controller
@RequestMapping("/user*")
public class UserController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * {@inheritDoc}
     */
    @InitBinder
    @Override
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(getText("date.time.format")), true));
    }

    /**
     * ユーザ画面初期処理.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return 画面入力値保持モデル
     * @throws IOException
     *             {@link IOException}
     */
    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    public User showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 管理者でない場合、自身以外のユーザを登録、更新することは出来ない
        if (!request.isUserInRole(Constants.ADMIN_ROLE)) {
            if (isAdd(request) || request.getParameter("userId") != null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                log.warn("User '" + request.getRemoteUser() + "' is trying to edit user with id '" + request.getParameter("id") + "'");
                throw new AccessDeniedException("You do not have permission to modify other users.");
            }
        }

        String userId = request.getParameter("userId");

        if (userId == null && !isAdd(request)) {
            return userManager.getUserByUsername(request.getRemoteUser());
        } else if (!StringUtils.isBlank(userId) && !"".equals(request.getParameter("version"))) {
            return userManager.getUser(userId);
        } else {
            User user = new User();
            user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
            user.addRole(new Role(Constants.USER_ROLE));
            return user;
        }
    }

    /**
     * ユーザ画面保存処理.
     *
     * @param user
     *            画面入力値保持モデル
     * @param result
     *            エラーチェック結果
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return 遷移先jsp名
     * @throws IOException
     *             {@link IOException}
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT })
    public String onSubmit(@ModelAttribute("user") @Valid User user, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                if (!error.getField().equals("password")) {
                    return "user";
                }
            }
        }

        if (request.isUserInRole(Constants.ADMIN_ROLE)) {
            userManager.activateRoles(user);
        } else {
            User cleanUser = userManager.getUserByUsername(request.getRemoteUser());
            user.setRoles(cleanUser.getRoles());
        }

        try {
            userManager.saveUser(user);
        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (DBException e) {
            rejectValue(result, e);

            return "user";
        }

        if (!StringUtils.equals(request.getParameter("from"), "list")) {
            saveFlashMessage(getText("updated"));

            return "redirect:/top";
        } else {
            if (StringUtils.isBlank(request.getParameter("version"))) {
                saveFlashMessage(getText("inserted"));

                // 登録完了メールを送信する
                userManager.sendCreatedUserEmail(user, RequestUtil.getAppURL(request) + UpdatePasswordController.RECOVERY_PASSWORD_TEMPLATE);

                return "redirect:/admin/master/users";
            } else {
                saveFlashMessage(getText("updated"));

                return "redirect:/admin/master/users";
            }
        }
    }

    /**
     * 画面の処理区分を取得する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return true:登録処理、false:編集処理
     */
    private boolean isAdd(HttpServletRequest request) {
        String method = request.getParameter("method");
        return method != null && method.equalsIgnoreCase("add");
    }
}
