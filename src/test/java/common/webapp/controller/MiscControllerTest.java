package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MiscControllerTest extends BaseControllerTestCase {

    @Autowired
    private MiscController c;

    @Test
    public void testTopRedirect() {
        String rtn = c.topRedirect();

        assertEquals("redirect:/top", rtn);
    }

    @Test
    public void testTopRequest() {
        String rtn = c.topRequest();

        assertEquals("top", rtn);
    }

    @Test
    public void testAdminTopRedirect() {
        String rtn = c.adminTopRedirect();

        assertEquals("redirect:/admin/top", rtn);
    }

    @Test
    public void testAdminTopRequest() {
        String rtn = c.adminTopRequest();

        assertEquals("admin/top", rtn);
    }

    @Test
    public void testAdminMasterTopRedirect() {
        String rtn = c.adminMasterTopRedirect();

        assertEquals("redirect:/admin/master/top", rtn);
    }

    @Test
    public void testAdminMasterTopRequest() {
        String rtn = c.adminMasterTopRequest();

        assertEquals("admin/master/top", rtn);
    }

    @Test
    public void testActiveUsersRequest() {
        String rtn = c.activeUsersRequest();

        assertEquals("admin/activeUsers", rtn);
    }

    @Test
    public void testSaveFlashError() {
        assertDoesNotThrow(() -> {
            c.saveFlashError(new RuntimeException("foo.bar"));
        });
    }
}
