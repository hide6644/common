package common.service;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import common.model.User;

public class PasswordTokenManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager userManager;

    @Autowired
    private PasswordTokenManager passwordTokenManager;

    private GreenMail greenMail;

    @Before
    public void setUp() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);

        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();

        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setHost("localhost");
    }

    @After
    public void tearDown() {
        greenMail.stop();
    }

    @Test
    public void testGenerateRecoveryToken() {
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));
    }

    @Test
    public void testConsumeRecoveryToken() {
        greenMail.reset();

        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));

        user = userManager.updatePassword(user.getUsername(), null, token, "pass");

        assertTrue(greenMail.getReceivedMessages().length == 1);
        assertFalse(passwordTokenManager.isRecoveryTokenValid(user, token));
    }
}
