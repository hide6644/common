package common.webapp.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.subethamail.wiser.Wiser;

import common.model.User;
import common.webapp.filter.FlashMap;

public class SignupControllerTest extends BaseControllerTestCase {

    @Autowired
    private SignupController c = null;

    private MockHttpServletRequest request;

    @Test
    public void testDisplayForm() throws Exception {
        User user = c.showForm();
        assertNotNull(user);
    }

    @Test
    public void testSignupUser() throws Exception {
        request = newGet("/userform.html");

        User user = new User();
        user.setUsername("self-registered");
        user.setPassword("Password1");
        user.setConfirmPassword("Password1");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("self-registered@localhost");

        // start SMTP Server
        Wiser wiser = new Wiser();
        wiser.setPort(getSmtpPort());
        wiser.start();
        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors, request);
        assertFalse("errors returned in model", errors.hasErrors());
        // verify an account information e-mail was sent
        wiser.stop();
        assertTrue(wiser.getMessages().size() == 1);
        // verify that success messages are in the request
        assertNotNull(FlashMap.get("flash_info_messages"));
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
