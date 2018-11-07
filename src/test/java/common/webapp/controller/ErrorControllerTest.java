package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;

public class ErrorControllerTest extends BaseControllerTestCase {

    @Autowired
    private ErrorController c;

    @Test
    public void testRenderErrorPage() {
        MockHttpServletRequest request = newGet("/error");
        request.setAttribute("javax.servlet.error.status_code", 400);
        ModelMap mm = new ExtendedModelMap();
        String rtn = c.renderErrorPage(mm, request);

        assertEquals("error", mm.get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 401);
        mm = new ExtendedModelMap();
        rtn = c.renderErrorPage(mm, request);

        assertEquals("403", mm.get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 403);
        mm = new ExtendedModelMap();
        rtn = c.renderErrorPage(mm, request);

        assertEquals("403", mm.get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 404);
        mm = new ExtendedModelMap();
        rtn = c.renderErrorPage(mm, request);

        assertEquals("404", mm.get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 500);
        mm = new ExtendedModelMap();
        rtn = c.renderErrorPage(mm, request);

        assertEquals("error", mm.get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 999);
        mm = new ExtendedModelMap();
        rtn = c.renderErrorPage(mm, request);

        assertEquals("error", mm.get("errorTitle"));
        assertEquals("error", rtn);
    }
}
