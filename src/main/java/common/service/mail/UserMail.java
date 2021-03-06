package common.service.mail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import common.entity.User;
import common.service.PasswordTokenManager;

/**
 * Userメール処理クラス.
 */
@Service("userMail")
public class UserMail {

    /** メールを処理するクラス */
    @Autowired(required = false)
    private MailEngine mailEngine;

    /** パスワードトークン処理のクラス */
    @Autowired(required = false)
    private PasswordTokenManager passwordTokenManager;

    /** Simple Mailメッセージ */
    @Autowired(required = false)
    private SimpleMailMessage mailMessage;

    /** メッセージソースアクセサー */
    private MessageSourceAccessor messages;

    /** ユーザ本登録メールのテンプレート */
    private String accountCreatedTemplate = "accountCreated.ftl";

    /** パスワード回復案内メールのテンプレート */
    private String passwordRecoveryTemplate = "passwordRecovery.ftl";

    /** パスワード更新メールのテンプレート */
    private String passwordUpdatedTemplate = "passwordUpdated.ftl";

    /**
     * ユーザ本登録用のURLをメールで送信する.
     *
     * @param user
     *            ユーザ
     */
    public void sendSignupEmail(User user) {
        sendEmail(user, accountCreatedTemplate, messages.getMessage("signupForm.email.subject"),
                messages.getMessage("signupForm.email.message"), buildRecoveryPasswordUrl(user, "/signupComplete"));
    }

    /**
     * ユーザ登録用のURLをメールで送信する.
     *
     * @param user
     *            ユーザ
     */
    public void sendCreatedEmail(User user) {
        sendEmail(user, accountCreatedTemplate, messages.getMessage("userSaveForm.email.subject"),
                messages.getMessage("userSaveForm.email.message"), buildRecoveryPasswordUrl(user, "/updatePassword"));
    }

    /**
     * パスワード再入力用のURLをメールで送信する.
     *
     * @param user
     *            ユーザ
     */
    public void sendPasswordRecoveryEmail(User user) {
        sendEmail(user, passwordRecoveryTemplate, messages.getMessage("passwordForm.email.subject"),
                messages.getMessage("passwordForm.recovery.email.message"), buildRecoveryPasswordUrl(user, "/updatePassword"));
    }

    /**
     * パスワード変更通知をメールで送信する.
     *
     * @param user
     *            ユーザ
     */
    public void sendUpdatePasswordEmail(User user) {
        sendEmail(user, passwordUpdatedTemplate, messages.getMessage("passwordForm.email.subject"), messages.getMessage("passwordForm.email.message"), "");
    }

    /**
     * ユーザのパスワード回復用のURLを生成する.
     *
     * @param user
     *            ユーザ
     * @param url
     *            URL
     * @return パスワード再入力用のURL
     */
    public String buildRecoveryPasswordUrl(User user, String url) {
        String token = passwordTokenManager.generateRecoveryToken(user);
        String username = user.getUsername();
        return messages.getMessage("company.url") + url + StringUtils.replaceEach("?username={username}&token={token}", new String[] { "{username}", "{token}" }, new String[] { username, token });
    }

    /**
     * メールを送信する.
     *
     * @param user
     *            ユーザ
     * @param template
     *            テンプレート
     * @param subject
     *            件名
     * @param message
     *            本文
     * @param url
     *            URL
     */
    public void sendEmail(User user, String template, String subject, String message, String url) {
        mailMessage.setSubject("[" + messages.getMessage("webapp.name") + "] " + subject);
        mailMessage.setTo(user.getEmail());

        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("message", message);
        model.put("URL", url);

        mailEngine.sendMessage(mailMessage, template, model);
    }

    /**
     * メッセージソースを設定する.
     *
     * @param messageSource
     *            メッセージソース
     */
    @Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }
}
