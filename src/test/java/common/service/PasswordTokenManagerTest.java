package common.service;

import static org.junit.Assert.*;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;

import common.model.User;

public class PasswordTokenManagerTest extends BaseManagerTestCase {

    protected transient final Log log = LogFactory.getLog(getClass());

    private int smtpPort = 25250;

    private UserManager userManager;

    private PasswordTokenManager passwordTokenManager;

    @Autowired
    @Qualifier("userManager")
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Autowired
    @Qualifier("passwordTokenManager")
    public void setPasswordTokenManager(PasswordTokenManager passwordTokenManager) {
        this.passwordTokenManager = passwordTokenManager;
    }

    @Before
    public void onSetUp() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        smtpPort = smtpPort + (int) (Math.random() * 100);
        // change the port on the mailSender so it doesn't conflict with an
        // existing SMTP server on localhost
        final JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(smtpPort);
        mailSender.setHost("localhost");
    }

    @Test
    public void testGenerateRecoveryToken() {
        User user = userManager.getUserByUsername("normaluser");
        String token = passwordTokenManager.generateRecoveryToken(user);
        Assert.assertNotNull(token);
        Assert.assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));
    }

    @Test
    public void testConsumeRecoveryToken() throws Exception {
        User user = userManager.getUserByUsername("normaluser");
        Integer version = user.getVersion();

        String token = passwordTokenManager.generateRecoveryToken(user);
        Assert.assertNotNull(token);
        Assert.assertTrue(passwordTokenManager.isRecoveryTokenValid(user, token));

        // start SMTP Server
        Wiser wiser = new Wiser();
        wiser.setPort(smtpPort);
        wiser.start();

        userManager.updatePassword(user.getUsername(), null, token, "pass", "");

        wiser.stop();
        assertTrue(wiser.getMessages().size() == 1);

        Assert.assertTrue(user.getVersion() > version);
        Assert.assertFalse(passwordTokenManager.isRecoveryTokenValid(user, token));
    }
}
