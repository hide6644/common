package common.webapp.controller;

import static org.junit.Assert.*;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.subethamail.wiser.Wiser;

import common.model.User;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

public class UpdatePasswordControllerTest extends BaseControllerTestCase {

    @Autowired
    private UpdatePasswordController controller;

    @Autowired
    private UserManager userManager;

    @Test
    public void testRequestRecoveryToken() throws Exception {
        String username = "administrator";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        // start SMTP Server
        Wiser wiser = new Wiser();
        wiser.setPort(getSmtpPort());
        wiser.start();
        controller.requestRecoveryToken(username, request);
        // verify an account information e-mail was sent
        wiser.stop();
        assertTrue(wiser.getMessages().size() == 1);
        // verify that success messages are in the session
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testShowUpdatePasswordForm() throws Exception {
        String username = "administrator";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        ModelAndView mav = controller.showForm(username, null, request);
        assertEquals("updatePassword", mav.getViewName());
    }

    @Test
    public void testShowResetPasswordForm() throws Exception {
        String username = "administrator";
        User user = userManager.getUserByUsername(username);
        String token = userManager.generateRecoveryToken(user);
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", token);
        ModelAndView mav = controller.showForm(username, token, request);
        assertEquals("updatePassword", mav.getViewName());
    }

    @Test
    public void testShowResetPasswordFormBadToken() throws Exception {
        String username = "administrator";
        String badtoken = RandomStringUtils.random(32);
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", badtoken);
        controller.showForm(username, badtoken, request);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testResetPassword() throws Exception {
        String username = "administrator";
        User user = userManager.getUserByUsername(username);
        String token = userManager.generateRecoveryToken(user);
        String password = "new-pass";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", token);
        request.addParameter("password", password);
        Wiser wiser = new Wiser();
        wiser.setPort(getSmtpPort());
        wiser.start();
        controller.onSubmit(username, token, null, password, request);
        wiser.stop();
        assertTrue(wiser.getMessages().size() == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));
        assertNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testResetPasswordBadToken() throws Exception {
        String username = "administrator";
        String badToken = RandomStringUtils.random(32);
        String password = "new-pass";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", badToken);
        request.addParameter("password", password);
        controller.onSubmit(username, badToken, null, password, request);
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
        controller.onSubmit(username, null, currentPassword, password, request);
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
        controller.onSubmit(username, null, currentPassword, password, request);
        assertNull(FlashMap.get("flash_info_messages"));
        assertNotNull(FlashMap.get("flash_error_messages"));
    }
}
