package common.webapp.controller;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import common.model.User;
import common.webapp.filter.FlashMap;

public class SignupControllerTest extends BaseControllerTestCase {

    @Autowired
    private SignupController c = null;

    @Test
    public void testDisplayForm() throws Exception {
        User user = c.showForm();
        assertNotNull(user);
    }

    @Test
    public void testSignupUser() throws Exception {
        User user = new User();
        user.setUsername("self-registered");
        user.setPassword("Password1");
        user.setConfirmPassword("Password1");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("self-registered@localhost");

        Wiser wiser = new Wiser();
        wiser.setPort(getSmtpPort());
        wiser.start();
        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors);
        assertFalse("errors returned in model", errors.hasErrors());

        List<WiserMessage> massageList = wiser.getMessages();
        String content = (String) massageList.get(0).getMimeMessage().getContent();
        String token = content.substring(content.indexOf("token=") + "token=".length()).replaceAll("\r\n", "");
        c.complete("self-registered", token);

        wiser.stop();
        assertTrue(wiser.getMessages().size() == 1);

        assertNotNull(FlashMap.get("flash_info_messages"));
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
