package common.webapp.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MiscControllerTest extends BaseControllerTestCase {

    @Autowired
    private MiscController c = null;

    @Test
    public void testTopRequest() throws Exception {
        String rtn = c.topRequest();

        assertEquals("top", rtn);
    }

    @Test
    public void testAdminTopRequest() throws Exception {
        String rtn = c.adminTopRequest();

        assertEquals("admin/top", rtn);
    }

    @Test
    public void testAdminMasterTopRequest() throws Exception {
        String rtn = c.adminMasterTopRequest();

        assertEquals("admin/master/top", rtn);
    }

    @Test
    public void testActiveUsersRequest() throws Exception {
        String rtn = c.activeUsersRequest();

        assertEquals("admin/activeUsers", rtn);
    }
}
