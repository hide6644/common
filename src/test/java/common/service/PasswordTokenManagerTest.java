package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import common.dto.PasswordForm;
import common.model.User;

public class PasswordTokenManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager userManager;

    @Autowired
    private PasswordTokenManager passwordTokenManager;

    private static GreenMail greenMail;

    @BeforeAll
    public static void setUpClass() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @BeforeEach
    public void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setHost("localhost");
    }

    @AfterAll
    public static void tearDownClass() {
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
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));

        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername(user.getUsername());
        passwordForm.setToken(token);
        passwordForm.setNewPassword("pass");
        user = userManager.updatePassword(passwordForm);

        assertTrue(greenMail.getReceivedMessages().length == 1);
        assertFalse(passwordTokenManager.isRecoveryTokenValid(user, token));
    }
}
