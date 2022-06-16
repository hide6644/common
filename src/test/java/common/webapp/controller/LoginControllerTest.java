package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;

import common.webapp.filter.FlashMap;

class LoginControllerTest extends BaseControllerTestCase {

    @Autowired
    private LoginController c;

    @Test
    void testSetupLogin() {
        String rtn = c.setupLogin(new ExtendedModelMap());

        assertEquals("login", rtn);
    }

    @Test
    void testLogin() throws Exception {
        boolean rememberMeFlg = true;
        String username = "user";
        String password = "pass";
        String rtn = c.login(rememberMeFlg, username, password);

        assertEquals("forward:/login?username=user&password=pass&remember-me=true", rtn);
    }

    @Test
    void testAccountDisabled() {
        String rtn = c.accountDisabled();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    void testAccountLocked() {
        String rtn = c.accountLocked();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    void testAccountExpired() {
        String rtn = c.accountExpired();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    void testCredentialsExpired() {
        String rtn = c.credentialsExpired();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }

    @Test
    void testBadCredentials() {
        String rtn = c.badCredentials();

        assertEquals("redirect:/login", rtn);
        assertNotNull(FlashMap.get("flash_error_messages"));
    }
}
