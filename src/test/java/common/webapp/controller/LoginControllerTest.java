package common.webapp.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import common.webapp.filter.FlashMap;

public class LoginControllerTest extends BaseControllerTestCase {

    @Autowired
    private LoginController c = null;

    @Test
    public void testSetupLogin() throws Exception {
        ModelAndView mav = c.setupLogin();

        assertEquals("login", mav.getViewName());
    }

    @Test
    public void testLogin() throws Exception {
        boolean rememberMeFlg = true;
        String username = "user";
        String password = "pass";
        String rtn = c.login(rememberMeFlg, username, password);

        assertEquals("forward:/login?username=user&password=pass&remember-me=true", rtn);
    }

    @Test
    public void testAccountDisabled() throws Exception {
        String rtn = c.accountDisabled();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testAccountLocked() throws Exception {
        String rtn = c.accountLocked();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testAccountExpired() throws Exception {
        String rtn = c.accountExpired();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testCredentialsExpired() throws Exception {
        String rtn = c.credentialsExpired();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    public void testBadCredentials() throws Exception {
        String rtn = c.badCredentials();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }
}