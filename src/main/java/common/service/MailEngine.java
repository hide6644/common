package common.service;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * メールを処理するクラス.
 */
public class MailEngine {

    /** ログ出力クラス */
    private final Logger log = LogManager.getLogger(getClass());

    /** メール送信処理クラス */
    private MailSender mailSender;

    /** テンプレートエンジン */
    private Configuration freemarkerConfiguration;;

    /** デフォルトの送信者 */
    private String defaultFrom;

    /**
     * メッセージを送信する.
     *
     * @param recipients
     *            宛先
     * @param sender
     *            送信者
     * @param resource
     *            添付ファイル
     * @param bodyText
     *            本文
     * @param subject
     *            件名
     * @param attachmentName
     *            添付ファイル名
     * @throws MessagingException
     *             {@link MessagingException}
     */
    public void sendMessage(String[] recipients, String sender, ClassPathResource resource, String bodyText, String subject, String attachmentName) throws MessagingException {
        MimeMessage message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipients);

        // 送信者が設定されていなかった場合
        if (sender == null) {
            helper.setFrom(defaultFrom);
        } else {
            helper.setFrom(sender);
        }

        helper.setText(bodyText);
        helper.setSubject(subject);

        helper.addAttachment(attachmentName, resource);

        ((JavaMailSenderImpl) mailSender).send(message);
    }

    /**
     * メッセージを送信する.
     *
     * @param msg
     *            Simple Mailメッセージモデル
     * @param templateName
     *            テンプレート
     * @param model
     *            入力値
     */
    public void sendMessage(SimpleMailMessage msg, String templateName, Map<String, Object> model) {
        String result = null;

        try {
            Template template = freemarkerConfiguration.getTemplate(templateName);
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        msg.setText(result);
        send(msg);
    }

    /**
     * メッセージを送信する.
     *
     * @param msg
     *            Simple Mailメッセージ
     * @throws MailException
     *             {@link MailException}
     */
    public void send(SimpleMailMessage msg) throws MailException {
        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    /**
     * メール送信処理クラスを設定する.
     *
     * @param mailSender
     *            メール送信処理クラス
     */
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * テンプレートエンジンを設定する.
     *
     * @param freemarkerConfiguration
     *            テンプレートエンジン
     */
    public void setFreemarkerConfiguration(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    /**
     * 送信者を設定する.
     *
     * @param from
     *            送信者
     */
    public void setFrom(String from) {
        this.defaultFrom = from;
    }
}
