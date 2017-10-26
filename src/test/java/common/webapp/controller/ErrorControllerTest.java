package common.webapp.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

public class ErrorControllerTest extends BaseControllerTestCase {

    @Autowired
    private ErrorController c = null;

    @Test
    public void testRenderErrorPage() {
        MockHttpServletRequest request = newGet("/error");
        request.setAttribute("javax.servlet.error.status_code", 400);
        ModelAndView mav = c.renderErrorPage(request);

        assertEquals("error", mav.getModelMap().get("errorTitle"));
        
        request.setAttribute("javax.servlet.error.status_code", 401);
        mav = c.renderErrorPage(request);

        assertEquals("403", mav.getModelMap().get("errorTitle"));
        
        request.setAttribute("javax.servlet.error.status_code", 403);
        mav = c.renderErrorPage(request);

        assertEquals("403", mav.getModelMap().get("errorTitle"));
        
        request.setAttribute("javax.servlet.error.status_code", 404);
        mav = c.renderErrorPage(request);

        assertEquals("404", mav.getModelMap().get("errorTitle"));
        
        request.setAttribute("javax.servlet.error.status_code", 500);
        mav = c.renderErrorPage(request);

        assertEquals("error", mav.getModelMap().get("errorTitle"));

        request.setAttribute("javax.servlet.error.status_code", 999);
        mav = c.renderErrorPage(request);

        assertEquals("error", mav.getModelMap().get("errorTitle"));
        assertEquals("error", mav.getViewName());
    }
}
