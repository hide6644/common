package common.webapp.filter;

import junit.framework.TestCase;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ServiceResponseFilterTest extends TestCase {

    private ServiceResponseFilter filter = null;

    protected void setUp() throws Exception {
        filter = new ServiceResponseFilter();
        filter.init(new MockFilterConfig());
    }

    public void testUnknownBrowser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent", "");

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());

        assertNull(response.getContentType());
    }

    public void testChromeBrowser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("test.csv");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36");

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());

        assertNotNull(response.getContentType());
        assertEquals("Application/Octet-Stream", response.getContentType());

        assertNotNull(response.getHeader("Content-Disposition"));
    }
}
