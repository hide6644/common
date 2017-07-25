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
    public void testRenderErrorPage() throws Exception {
        MockHttpServletRequest request = newGet("/error");
        request.setAttribute("javax.servlet.error.status_code", 400);
        ModelAndView mav = c.renderErrorPage(request);

        assertEquals("Http Error Code: 400. Bad Request", mav.getModelMap().get("errorMsg"));
        
        request.setAttribute("javax.servlet.error.status_code", 401);
        mav = c.renderErrorPage(request);

        assertEquals("Http Error Code: 401. Unauthorized", mav.getModelMap().get("errorMsg"));
        
        request.setAttribute("javax.servlet.error.status_code", 403);
        mav = c.renderErrorPage(request);

        assertEquals("Http Error Code: 403. Forbidden", mav.getModelMap().get("errorMsg"));
        
        request.setAttribute("javax.servlet.error.status_code", 404);
        mav = c.renderErrorPage(request);

        assertEquals("Http Error Code: 404. Resource not found", mav.getModelMap().get("errorMsg"));
        
        request.setAttribute("javax.servlet.error.status_code", 500);
        mav = c.renderErrorPage(request);

        assertEquals("Http Error Code: 500. Internal Server Error", mav.getModelMap().get("errorMsg"));
        
        request.setAttribute("javax.servlet.error.status_code", 999);
        mav = c.renderErrorPage(request);

        assertEquals("error", mav.getViewName());
    }
}
