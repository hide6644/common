package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import jakarta.mail.BodyPart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import common.service.mail.MailEngine;

class MailEngineTest extends BaseManagerTestCase {

    @Autowired
    private MailEngine mailEngine;

    @Autowired
    private SimpleMailMessage mailMessage;

    private static GreenMail greenMail;

    @BeforeAll
    static void setUpClass() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setHost("localhost");
        mailEngine.setMailSender(mailSender);
    }

    @AfterAll
    static void tearDownClass() {
        greenMail.stop();
    }

    @Test
    void testSend() throws Exception {
        Date dte = new Date();
        mailMessage.setTo("foo@bar.com");
        String emailSubject = "grepster testSend: " + dte;
        String emailBody = "Body of the grepster testSend message sent at: " + dte;
        mailMessage.setSubject(emailSubject);
        mailMessage.setText(emailBody);
        mailEngine.send(mailMessage);

        ClassPathResource cpResource = new ClassPathResource("/test-attachment.txt");
        mailEngine.send(mailMessage, emailBody, cpResource.getURL().getPath());

        assertEquals(2, greenMail.getReceivedMessages().length);

        MimeMessage mm = greenMail.getReceivedMessages()[0];
        assertEquals(emailSubject, mm.getSubject());
        assertEquals(emailBody, mm.getContent());
    }

    @Test
    void testSendMessageWithAttachment() throws Exception {
        final String attachmentName = "boring-attachment.txt";

        Date dte = new Date();
        String emailSubject = "grepster testSendMessageWithAttachment: " + dte;
        String emailBody = "Body of the grepster testSendMessageWithAttachment message sent at: " + dte;

        ClassPathResource cpResource = new ClassPathResource("/test-attachment.txt");
        // a null from should work
        mailEngine.sendMessage(new String[] { "foo@bar.com" }, null, emailBody, emailSubject, attachmentName, cpResource);
        mailEngine.sendMessage(new String[] { "foo@bar.com" }, mailMessage.getFrom(), emailBody, emailSubject, attachmentName, cpResource);

        // one without and one with from
        assertEquals(2, greenMail.getReceivedMessages().length);

        MimeMessage mm = greenMail.getReceivedMessages()[0];
        Object o = mm.getContent();

        assertTrue(o instanceof MimeMultipart);

        MimeMultipart multi = (MimeMultipart) o;
        int numOfParts = multi.getCount();
        boolean hasTheAttachment = false;

        for (int i = 0; i < numOfParts; i++) {
            BodyPart bp = multi.getBodyPart(i);
            String disp = bp.getDisposition();
            if (disp == null) { // the body of the email
                Object innerContent = bp.getContent();
                MimeMultipart innerMulti = (MimeMultipart) innerContent;

                assertEquals(emailBody, innerMulti.getBodyPart(0).getContent());
            } else if (disp.equals(Part.ATTACHMENT)) { // the attachment to the email
                hasTheAttachment = true;

                assertEquals(attachmentName, bp.getFileName());
            } else {
                fail("Did not expect to be able to get here.");
            }
        }

        assertTrue(hasTheAttachment);
        assertEquals(emailSubject, mm.getSubject());
    }
}
