package common.webapp.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import common.Constants;
import common.exception.DatabaseException;
import common.model.Role;
import common.model.User;
import common.service.UserManager;
import common.service.mail.UserMail;

/**
 * ユーザ登録情報変更処理クラス.
 */
@Controller
@RequestMapping("/user*")
public class UserController extends BaseController {

    /** Userメール処理クラス */
    @Autowired
    private UserMail userMail;

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
        String userId = request.getParameter("userId");

        // 管理者でない場合、自身以外のユーザを登録、更新することは出来ない
        if (!request.isUserInRole(Constants.ADMIN_ROLE) && (Objects.equals(request.getParameter("mode"), "Add") || userId != null)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            log.warn("User '" + request.getRemoteUser() + "' is trying to edit user with id '" + request.getParameter("id") + "'");
            throw new AccessDeniedException("You do not have permission to modify other users.");
        }

        if (Objects.equals(request.getParameter("mode"), "Add")) {
            User user = new User();
            user.setCredentialsExpiredDate(LocalDateTime.now().plusDays(Constants.CREDENTIALS_EXPIRED_TERM));
            user.addRole(new Role(Constants.USER_ROLE));
            return user;
        } else if (userId != null) {
            return userManager.getUser(userId);
        } else {
            return userManager.getUserByUsername(request.getRemoteUser());
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
     * @return 遷移先
     * @throws IOException
     *             {@link IOException}
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT })
    public String onSubmit(@ModelAttribute("user") @Valid User user, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (result.hasErrors() && result.getFieldErrors().stream().anyMatch(error -> !error.getField().equals("password"))) {
            return "user";
        }

        try {
            User managedUser = userManager.saveUser(user);

            if (Objects.equals(request.getParameter("from"), "list")) {
                if (Objects.equals(request.getParameter("mode"), "Add")) {
                    saveFlashMessage(getText("inserted"));

                    // 登録完了メールを送信する
                    userMail.sendCreatedEmail(managedUser);
                } else {
                    saveFlashMessage(getText("updated"));
                }

                return "redirect:/admin/master/users";
            } else {
                saveFlashMessage(getText("updated"));
                return "redirect:/top";
            }
        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (DatabaseException e) {
            log.error(e);
            return "user";
        }
    }
}
