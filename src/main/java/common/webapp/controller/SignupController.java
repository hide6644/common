package common.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import common.Constants;
import common.exception.DBException;
import common.model.Role;
import common.model.User;
import common.service.RoleManager;
import common.service.UserManager;
import common.webapp.util.RequestUtil;

/**
 * ユーザ登録画面処理クラス.
 *
 * @author hide6644
 */
@Controller
public class SignupController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

    /**
     * ユーザ登録画面初期処理.
     *
     * @param model
     *            画面汎用値保持モデル
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

        user.setCredentialsExpiredDate(new DateTime().plusDays(Constants.CREDENTIALS_EXPIRED_TERM).toDate());
        // 新規登録時は権限を一般で設定する
        user.getRoles().clear();
        user.addRole(roleManager.getRole(Constants.USER_ROLE));

        try {
            userManager.saveUser(user);
            saveFlashMessage(getText("signupForm.provisional"));
        } catch (DBException e) {
            rejectValue(result, e);

            return "signup";
        }

        // 登録完了メールを送信する
        mailMessage.setSubject("[" + getText("webapp.name") + "] " + getText("signupForm.email.subject"));
        mailMessage.setTo(user.getEmail());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("message", getText("signupForm.email.message"));
        model.put("attention", getText("signupForm.email.attention"));

        try {
            StringBuilder msg = new StringBuilder();
            msg.append(RequestUtil.getAppURL(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
            URLCodec codec = new URLCodec();
            msg.append("/signupComplete?username=").append(codec.encode(user.getUsername()));
            msg.append("&key=").append(codec.encode(user.getPassword()));
            model.put("URL", msg.toString());

            mailEngine.sendMessage(mailMessage, "accountCreated.vm", model);
        } catch (MailException e) {
            if (log.isWarnEnabled()) {
                log.warn(e);
            }

            saveFlashError(getText("errors.send.email"));
        } catch (EncoderException e) {
            log.error(e);
        }

        return "redirect:/login";
    }

    /**
     * ユーザ登録完了処理
     *
     * @param username
     *            ユーザ名
     * @param key
     *            ユーザ認証用キー
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "signupComplete", method = RequestMethod.GET)
    public String complete(@RequestParam("username") String username, @RequestParam("key") String key) {
        try {
            User user = userManager.getUser(username);
            URLCodec codec = new URLCodec();

            if (!user.getPassword().equals(codec.decode(key))) {
                return "redirect:/login";
            }

            // 登録した"username"、"password"でログイン処理を行う
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
            auth.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(auth);

            user.setEnabled(true);

            userManager.saveUser(user);
            saveFlashMessage(getText("signupForm.complete"));
        } catch (DBException e) {
            log.error(e);

            return "redirect:/login";
        } catch (DecoderException e) {
            log.error(e);

            return "redirect:/login";
        }

        return "redirect:/top";
    }
}
