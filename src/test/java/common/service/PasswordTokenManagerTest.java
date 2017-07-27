package common.service;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;

import common.model.User;

public class PasswordTokenManagerTest extends BaseManagerTestCase {

    private int smtpPort = 25250;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PasswordTokenManager passwordTokenManager;

    @Before
    public void setUp() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        smtpPort = smtpPort + (int) (Math.random() * 100);

        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(smtpPort);
        mailSender.setHost("localhost");
    }

    @Test
    public void testGenerateRecoveryToken() {
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));
    }

    @Test
    public void testConsumeRecoveryToken() throws Exception {
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));

        Wiser wiser = new Wiser();
        wiser.setPort(smtpPort);
        wiser.start();
        user = userManager.updatePassword(user.getUsername(), null, token, "pass");
        wiser.stop();

        assertTrue(wiser.getMessages().size() == 1);
        assertFalse(passwordTokenManager.isRecoveryTokenValid(user, token));
    }
}
