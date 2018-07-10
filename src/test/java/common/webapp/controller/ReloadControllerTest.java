package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

public class ReloadControllerTest extends BaseControllerTestCase {

    @Autowired
    private ReloadController c;

    @Test
    public void testHandleRequest() throws Exception {
        MockHttpServletRequest request = newGet("/admin/reload");
        String rtn = c.handleRequest(request);

        assertEquals("redirect:/top", rtn);
    }
}
