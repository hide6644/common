package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MiscControllerTest extends BaseControllerTestCase {

    @Autowired
    private MiscController c;

    @Test
    void testTopRedirect() {
        String rtn = c.topRedirect();

        assertEquals("redirect:/top", rtn);
    }

    @Test
    void testTopRequest() {
        String rtn = c.topRequest();

        assertEquals("top", rtn);
    }

    @Test
    void testAdminTopRedirect() {
        String rtn = c.adminTopRedirect();

        assertEquals("redirect:/admin/top", rtn);
    }

    @Test
    void testAdminTopRequest() {
        String rtn = c.adminTopRequest();

        assertEquals("admin/top", rtn);
    }

    @Test
    void testAdminMasterTopRedirect() {
        String rtn = c.adminMasterTopRedirect();

        assertEquals("redirect:/admin/master/top", rtn);
    }

    @Test
    void testAdminMasterTopRequest() {
        String rtn = c.adminMasterTopRequest();

        assertEquals("admin/master/top", rtn);
    }

    @Test
    void testActiveUsersRequest() {
        String rtn = c.activeUsersRequest();

        assertEquals("admin/activeUsers", rtn);
    }

    @Test
    void testSaveFlashError() {
        assertDoesNotThrow(() -> {
            c.saveFlashError(new RuntimeException("foo.bar"));
        });
    }
}
