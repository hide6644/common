package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import common.dto.PasswordForm;
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
    public void testRequestRecoveryToken() {
        String username = "administrator";

        c.requestRecoveryToken(username);

        assertTrue(greenMail.getReceivedMessages().length == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testShowUpdatePasswordForm() {
        String username = "administrator";
        MockHttpServletRequest request = newGet("/updatePassword");
        request.setRemoteUser(username);

        String rtn = c.showForm(null, null, new ExtendedModelMap(), request);

        assertEquals("password", rtn);
    }

    @Test
    public void testShowResetPasswordForm() {
        String username = "administrator";
        String token = passwordTokenManager.generateRecoveryToken(userManager.getUserByUsername(username));
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", token);

        String rtn = c.showForm(username, token, new ExtendedModelMap(), request);

        assertEquals("password", rtn);
    }

    @Test
    public void testShowResetPasswordFormBadToken() {
        String username = "administrator";
        String badtoken = new RandomStringGenerator.Builder().build().generate(32);
        MockHttpServletRequest request = newGet("/updatePassword");
        request.addParameter("username", username);
        request.addParameter("token", badtoken);

        c.showForm(username, badtoken, new ExtendedModelMap(), request);

        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testResetPassword() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setToken(passwordTokenManager.generateRecoveryToken(userManager.getUserByUsername(passwordForm.getUsername())));
        passwordForm.setNewPassword("new-pass");
        passwordForm.setConfirmPassword("new-pass");

        BindingResult errors = new DataBinder(passwordForm).getBindingResult();
        c.onSubmit(passwordForm, errors, request);

        assertTrue(greenMail.getReceivedMessages().length == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));
        assertNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testResetPasswordBadToken() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setToken(new RandomStringGenerator.Builder().build().generate(32));
        passwordForm.setNewPassword("new-pass");
        passwordForm.setConfirmPassword("new-pass");

        BindingResult errors = new DataBinder(passwordForm).getBindingResult();
        c.onSubmit(passwordForm, errors, request);

        assertNull(FlashMap.get("flash_info_messages"));
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testUpdatePassword() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setCurrentPassword("administrator");
        passwordForm.setNewPassword("new-administrator");
        passwordForm.setConfirmPassword("new-administrator");
        request.setRemoteUser(passwordForm.getUsername()); // user must ge logged in

        BindingResult errors = new DataBinder(passwordForm).getBindingResult();
        c.onSubmit(passwordForm, errors, request);

        assertNotNull(FlashMap.get("flash_info_messages"));
        assertNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testUpdatePasswordFromList() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setCurrentPassword("administrator");
        passwordForm.setNewPassword("new-administrator");
        passwordForm.setConfirmPassword("new-administrator");
        request.setRemoteUser(passwordForm.getUsername()); // user must ge logged in
        request.addParameter("from", "list");

        BindingResult errors = new DataBinder(passwordForm).getBindingResult();
        c.onSubmit(passwordForm, errors, request);

        assertNotNull(FlashMap.get("flash_info_messages"));
        assertNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testUpdatePasswordEmptyPassword() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setCurrentPassword("administrator");
        request.setRemoteUser(passwordForm.getUsername()); // user must ge logged in

        BindingResult errors = new DataBinder(passwordForm).getBindingResult();
        errors.rejectValue("newPassword", "errors.required", "{0} is a required field.");
        String rtn = c.onSubmit(passwordForm, errors, request);

        assertEquals("password", rtn);
    }

    @Test
    public void testUpdatePasswordBadCurrentPassword() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setCurrentPassword("bad-administrator");
        passwordForm.setNewPassword("new-administrator");
        passwordForm.setConfirmPassword("new-administrator");
        request.setRemoteUser(passwordForm.getUsername()); // user must ge logged in

        BindingResult errors = new DataBinder(passwordForm).getBindingResult();
        c.onSubmit(passwordForm, errors, request);

        assertNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testUpdatePasswordWithoutPermission() {
        MockHttpServletRequest request = newGet("/updatePassword");
        PasswordForm passwordForm = new PasswordForm();
        passwordForm.setUsername("administrator");
        passwordForm.setCurrentPassword("administrator");
        passwordForm.setNewPassword("new-administrator");
        passwordForm.setConfirmPassword("new-administrator");
        request.setRemoteUser("testuser"); // user must ge logged in

        try {
            BindingResult errors = new DataBinder(passwordForm).getBindingResult();
            c.onSubmit(passwordForm, errors, request);
            fail("AccessDeniedException not thrown...");
        } catch (AccessDeniedException ade) {
            assertNotNull(ade.getMessage());
        }
    }
}
