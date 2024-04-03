package common.webapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import common.Constants;
import common.dto.UserDetailsForm;
import common.entity.Role;
import common.exception.DatabaseException;
import common.service.UserManager;
import common.service.mail.UserMail;
import common.validator.groups.Modify;
import common.webapp.propertyeditors.LocalDateTimeEditor;
import lombok.extern.log4j.Log4j2;

/**
 * ユーザ登録情報変更処理クラス.
 */
@Controller
@RequestMapping("/user")
@Log4j2
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
    @Override
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimeEditor(DateTimeFormatter.ofPattern(getText("date.time.format")), true));
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
    @GetMapping
    @ModelAttribute("user")
    public UserDetailsForm showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = request.getParameter("userId");

        // 管理者でない場合、自身以外のユーザを登録、更新することは出来ない
        if (!request.isUserInRole(Constants.ADMIN_ROLE) && (Objects.equals(request.getParameter("mode"), "Add") || userId != null)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            log.warn("User '{}' is trying to edit user with id '{}'", () -> request.getRemoteUser(), () -> request.getParameter("id"));
            throw new AccessDeniedException("You do not have permission to modify other users.");
        }

        if (Objects.equals(request.getParameter("mode"), "Add")) {
            var userDetailsForm = new UserDetailsForm();
            userDetailsForm.setCredentialsExpiredDate(LocalDateTime.now().plusDays(Constants.CREDENTIALS_EXPIRED_TERM));
            userDetailsForm.addRole(new Role(Constants.USER_ROLE));
            return userDetailsForm;
        } else if (userId != null) {
            return userManager.getUserDetails(userManager.getUser(userId));
        } else {
            return userManager.getUserDetails(userManager.getUserByUsername(request.getRemoteUser()));
        }
    }

    /**
     * ユーザ登録処理.
     *
     * @param userDetailsForm
     *            ユーザ情報
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
    @PostMapping
    public String onSubmitByPostMethod(@ModelAttribute("user") @Validated UserDetailsForm userDetailsForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (result.hasErrors()) {
            return "user";
        }

        try {
            var managedUser = userManager.saveUserDetails(userDetailsForm);
            saveFlashMessage(getText("inserted"));

            // 登録完了メールを送信する
            userMail.sendCreatedEmail(managedUser);

            return "redirect:/admin/master/users";
        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (DatabaseException e) {
            log.error(e);
            return "user";
        }
    }

    /**
     * ユーザ更新処理.
     *
     * @param userDetailsForm
     *            ユーザ情報
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
    @PutMapping
    public String onSubmitByPutMethod(@ModelAttribute("user") @Validated(Modify.class) UserDetailsForm userDetailsForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (result.hasErrors()) {
            return "user";
        }

        try {
            userManager.saveUserDetails(userDetailsForm);
            saveFlashMessage(getText("updated"));

            if (Objects.equals(request.getParameter("from"), "list")) {
                return "redirect:/admin/master/users";
            } else {
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
