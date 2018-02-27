package common.service;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Random;

import javax.mail.BodyPart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import common.service.mail.MailEngine;

public class MailEngineTest extends BaseManagerTestCase {

    private int smtpPort = 25250;

    @Autowired
    private MailEngine mailEngine;

    @Autowired
    private SimpleMailMessage mailMessage;

    private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    @Before
    public void setUp() {
        mailSender.setHost("localhost");
        mailEngine.setMailSender(mailSender);
    }

    @After
    public void tearDown() {
        mailEngine.setMailSender(null);
    }

    @Test
    public void testSend() throws Exception {
        // mock smtp server
        Wiser wiser = new Wiser();
        // set the port to a random value so there's no conflicts between tests
        int port = smtpPort + new Random().nextInt(100);
        mailSender.setPort(port);
        wiser.setPort(port);
        wiser.start();

        Date dte = new Date();
        mailMessage.setTo("foo@bar.com");
        String emailSubject = "grepster testSend: " + dte;
        String emailBody = "Body of the grepster testSend message sent at: " + dte;
        mailMessage.setSubject(emailSubject);
        mailMessage.setText(emailBody);
        mailEngine.send(mailMessage);

        ClassPathResource cpResource = new ClassPathResource("/test-attachment.txt");
        mailEngine.send(mailMessage, emailBody, cpResource.getURL().getPath());

        wiser.stop();

        assertTrue(wiser.getMessages().size() == 2);
        WiserMessage wm = wiser.getMessages().get(0);
        assertEquals(emailSubject, wm.getMimeMessage().getSubject());
        assertEquals(emailBody + "\r\n", wm.getMimeMessage().getContent());
    }

    @Test
    public void testSendMessageWithAttachment() throws Exception {
        final String attachmentName = "boring-attachment.txt";

        //mock smtp server
        Wiser wiser = new Wiser();
        int port = smtpPort + new Random().nextInt(100);
        mailSender.setPort(port);
        wiser.setPort(port);
        wiser.start();

        Date dte = new Date();
        String emailSubject = "grepster testSendMessageWithAttachment: " + dte;
        String emailBody = "Body of the grepster testSendMessageWithAttachment message sent at: " + dte;

        ClassPathResource cpResource = new ClassPathResource("/test-attachment.txt");
        // a null from should work
        mailEngine.sendMessage(new String[] { "foo@bar.com" }, null, emailBody, emailSubject, attachmentName, cpResource);
        mailEngine.sendMessage(new String[] { "foo@bar.com" }, mailMessage.getFrom(), emailBody, emailSubject, attachmentName, cpResource);

        wiser.stop();
        // one without and one with from
        assertTrue(wiser.getMessages().size() == 2);

        WiserMessage wm = wiser.getMessages().get(0);
        Object o = wm.getMimeMessage().getContent();

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
        assertEquals(emailSubject, wm.getMimeMessage().getSubject());
    }
}
