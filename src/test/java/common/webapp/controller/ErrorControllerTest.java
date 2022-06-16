package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

class ErrorControllerTest extends BaseControllerTestCase {

    @Autowired
    private ErrorController c;

    @Test
    void testRenderErrorPage() {
        MockHttpServletRequest request = newGet("/error");
        request.setAttribute("javax.servlet.error.status_code", 400);
        Model model = new ExtendedModelMap();
        String rtn = c.renderErrorPage(model, request);

        assertEquals("error", model.asMap().get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 401);
        model = new ExtendedModelMap();
        rtn = c.renderErrorPage(model, request);

        assertEquals("403", model.asMap().get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 403);
        model = new ExtendedModelMap();
        rtn = c.renderErrorPage(model, request);

        assertEquals("403", model.asMap().get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 404);
        model = new ExtendedModelMap();
        rtn = c.renderErrorPage(model, request);

        assertEquals("404", model.asMap().get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 500);
        model = new ExtendedModelMap();
        rtn = c.renderErrorPage(model, request);

        assertEquals("error", model.asMap().get("errorTitle"));
        assertEquals("error", rtn);

        request.setAttribute("javax.servlet.error.status_code", 999);
        model = new ExtendedModelMap();
        rtn = c.renderErrorPage(model, request);

        assertEquals("error", model.asMap().get("errorTitle"));
        assertEquals("error", rtn);
    }
}
