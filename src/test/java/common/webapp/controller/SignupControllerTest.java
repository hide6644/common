package common.webapp.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import common.model.User;
import common.webapp.filter.FlashMap;

public class SignupControllerTest extends BaseControllerTestCase {

    @Autowired
    private SignupController c = null;

    @Test
    public void testDisplayForm() {
        User user = c.showForm();
        assertNotNull(user);
    }

    @Test
    public void testSignupUser() throws Exception {
        greenMail.reset();

        User user = new User();
        user.setUsername("self-registered");
        user.setPassword("Password1");
        user.setConfirmPassword("Password1");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("self-registered@localhost");

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors);

        assertFalse("errors returned in model", errors.hasErrors());

        String content = (String) greenMail.getReceivedMessages()[0].getContent();
        String token = content.substring(content.indexOf("token=") + "token=".length()).replaceAll("\r\n", "");
        c.complete("self-registered", token);

        assertTrue(greenMail.getReceivedMessages().length == 1);
        assertNotNull(FlashMap.get("flash_info_messages"));

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
