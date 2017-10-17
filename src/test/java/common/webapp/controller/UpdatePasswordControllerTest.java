package common.webapp.controller;

import static org.junit.Assert.*;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.subethamail.wiser.Wiser;

import common.service.PasswordTokenManager;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

public class UpdatePasswordControllerTest extends BaseControllerTestCase {

    @Autowired
    private UpdatePasswordController c;

    @Autowired
    private PasswordTokenManager passwordTokenManager;

    @Autowired
    private UserManager userManager;

    @Test
    public void testRequestRecoveryToken() throws Exception {
        String username = "administrator";

        Wiser wiser = new Wiser();
        wiser.setPort(getSmtpPort());
        wiser.start();
        c.requestRecoveryToken(username);
        wiser.stop();

        assertTrue(wiser.getMessages().size() == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testShowUpdatePasswordForm() throws Exception {
        String username = "administrator";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.setRemoteUser(username);

        ModelAndView mav = c.showForm(null, null, request);

        assertEquals("password", mav.getViewName());
    }

    @Test
    public void testShowResetPasswordForm() throws Exception {
        String username = "administrator";
        String token = passwordTokenManager.generateRecoveryToken(userManager.getUserByUsername(username));
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", token);

        ModelAndView mav = c.showForm(username, token, request);

        assertEquals("password", mav.getViewName());
    }

    @Test
    public void testShowResetPasswordFormBadToken() throws Exception {
        String username = "administrator";
        String badtoken = new RandomStringGenerator.Builder().build().generate(32);
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", badtoken);

        c.showForm(username, badtoken, request);

        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testResetPassword() throws Exception {
        String username = "administrator";
        String token = passwordTokenManager.generateRecoveryToken(userManager.getUserByUsername(username));
        String password = "new-pass";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", token);
        request.addParameter("password", password);

        Wiser wiser = new Wiser();
        wiser.setPort(getSmtpPort());
        wiser.start();
        c.onSubmit(username, token, null, password, request);
        wiser.stop();

        assertTrue(wiser.getMessages().size() == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));
        assertNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testResetPasswordBadToken() throws Exception {
        String username = "administrator";
        String badToken = new RandomStringGenerator.Builder().build().generate(32);
        String password = "new-pass";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", badToken);
        request.addParameter("password", password);

        c.onSubmit(username, badToken, null, password, request);

        assertNull(FlashMap.get("flash_info_messages"));
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testUpdatePassword() throws Exception {
        String username = "administrator";
        String currentPassword = "administrator";
        String password = "new-administrator";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.setRemoteUser(username);// user must ge logged in
        request.addParameter("username", username);
        request.addParameter("currentPassword", currentPassword);
        request.addParameter("password", password);

        c.onSubmit(username, null, currentPassword, password, request);

        assertNotNull(FlashMap.get("flash_info_messages"));
        assertNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testUpdatePasswordBadCurrentPassword() throws Exception {
        String username = "administrator";
        String currentPassword = "bad-administrator";
        String password = "new-administrator";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.setRemoteUser(username);// user must ge logged in
        request.addParameter("username", username);
        request.addParameter("currentPassword", currentPassword);
        request.addParameter("password", password);

        c.onSubmit(username, null, currentPassword, password, request);

        assertNull(FlashMap.get("flash_info_messages"));
    }
}
