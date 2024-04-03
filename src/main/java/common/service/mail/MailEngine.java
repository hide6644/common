package common.service.mail;

import java.util.Map;

import jakarta.mail.MessagingException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import no.api.freemarker.java8.Java8ObjectWrapper;
import lombok.extern.log4j.Log4j2;

/**
 * メールを処理するクラス.
 */
@Log4j2
public class MailEngine {

    /** メール送信処理クラス */
    private MailSender mailSender;

    /** テンプレートエンジン */
    private Configuration freemarkerConfiguration;

    /** デフォルトの送信者 */
    private String defaultFrom;

    /**
     * メッセージを送信する.
     *
     * @param recipients
     *            宛先
     * @param sender
     *            送信者
     * @param bodyText
     *            本文
     * @param subject
     *            件名
     * @param attachmentName
     *            添付ファイル名
     * @param resource
     *            添付ファイル
     * @throws MessagingException
     *             {@link MessagingException}
     */
    public void sendMessage(String[] recipients, String sender, String bodyText, String subject, String attachmentName, ClassPathResource resource) throws MessagingException {
        var message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
        var helper = new MimeMessageHelper(message, true);

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
     * @param simpleMailMessage
     *            Simple Mailメッセージモデル
     * @param templateName
     *            テンプレート
     * @param model
     *            入力値
     */
    public void sendMessage(SimpleMailMessage simpleMailMessage, String templateName, Map<String, Object> model) {
        try {
            freemarkerConfiguration.setObjectWrapper(new Java8ObjectWrapper(Configuration.VERSION_2_3_30));
            simpleMailMessage.setText(FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(templateName), model));
            send(simpleMailMessage);
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * メッセージを送信する.
     *
     * @param simpleMailMessage
     *            Simple Mailメッセージ
     * @param bodyText
     *            本文
     * @param attachmentFilePath
     *            添付ファイルのパス
     * @throws MessagingException
     *             {@link MessagingException}
     */
    public void send(SimpleMailMessage simpleMailMessage, String bodyText, String attachmentFilePath) throws MessagingException {
        var message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
        var helper = new MimeMessageHelper(message, true);

        helper.setTo(simpleMailMessage.getTo());
        helper.setFrom(simpleMailMessage.getFrom());
        helper.setSubject(simpleMailMessage.getSubject());
        helper.setText(bodyText);

        var file = new FileSystemResource(attachmentFilePath);
        helper.addAttachment(file.getFilename(), file);

        ((JavaMailSenderImpl) mailSender).send(message);
    }

    /**
     * メッセージを送信する.
     *
     * @param simpleMailMessage
     *            Simple Mailメッセージ
     */
    public void send(SimpleMailMessage simpleMailMessage) {
        mailSender.send(simpleMailMessage);
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
