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
import common.entity.User;

class PasswordTokenManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager userManager;

    @Autowired
    private PasswordTokenManager passwordTokenManager;

    private static GreenMail greenMail;

    @BeforeAll
    static void setUpClass() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setHost("localhost");
    }

    @AfterAll
    static void tearDownClass() {
        greenMail.stop();
    }

    @Test
    void testGenerateRecoveryToken() {
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));
    }

    @Test
    void testConsumeRecoveryToken() {
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);

        assertNotNull(token);
        assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));

        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername(user.getUsername());
        passwordForm.setToken(token);
        passwordForm.setNewPassword("pass");
        user = userManager.updatePassword(passwordForm);

        assertEquals(1, greenMail.getReceivedMessages().length);
        assertFalse(passwordTokenManager.isRecoveryTokenValid(user, token));
    }
}
