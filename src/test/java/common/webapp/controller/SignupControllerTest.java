package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import common.dto.SignupUserForm;
import common.webapp.filter.FlashMap;

public class SignupControllerTest extends BaseControllerTestCase {

    @Autowired
    private SignupController c;

    @Test
    public void testShowForm() {
        SignupUserForm signupUser = c.showForm();
        assertNotNull(signupUser);
    }

    @Test
    public void testSignupUserHasErrors() {
        SignupUserForm signupUser = new SignupUserForm();
        signupUser.setUsername("testuser");

        BindingResult errors = new DataBinder(signupUser).getBindingResult();
        errors.rejectValue("email", "errors.required", "{0} is a required field.");
        String rtn = c.onSubmit(signupUser, errors);

        assertEquals("signup", rtn);
    }

    @Test
    public void testCompleteHasErrors() {
        String rtn = c.complete("normaluser", "test-token");

        assertEquals("redirect:/login", rtn);
    }

    @Test
    public void testSignupUser() throws Exception {
        SignupUserForm signupUser = new SignupUserForm();
        signupUser.setUsername("self-registered");
        signupUser.setPassword("Password1");
        signupUser.setConfirmPassword("Password1");
        signupUser.setFirstName("First");
        signupUser.setLastName("Last");
        signupUser.setEmail("self-registered@localhost");

        BindingResult errors = new DataBinder(signupUser).getBindingResult();
        c.onSubmit(signupUser, errors);

        assertFalse(errors.hasErrors());

        String content = (String) greenMail.getReceivedMessages()[0].getContent();
        String token = content.substring(content.indexOf("token=") + "token=".length()).replaceAll("\r\n", "");
        c.complete("self-registered", token);

        assertTrue(greenMail.getReceivedMessages().length == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
