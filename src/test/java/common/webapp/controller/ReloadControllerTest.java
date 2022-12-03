package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

class ReloadControllerTest extends BaseControllerTestCase {

    @Autowired
    private ReloadController c;

    @Test
    void testHandleRequest() throws Exception {
        MockHttpServletRequest request = newGet("/admin/reload");
        String rtn = c.handleRequest(request);

        assertEquals("redirect:/top", rtn);
    }

    @Test
    void testHandleRequestWithReferer() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(newGet("/admin/reload").getSession());
        when(request.getHeader("Referer")).thenReturn("http://foo.bar/common/test");
        when(request.getContextPath()).thenReturn("/common");
        String rtn = c.handleRequest(request);

        assertEquals("redirect:/test", rtn);
    }
}
