package common.webapp.filter;

import javax.servlet.ServletRequest;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import junit.framework.TestCase;

public class FlashMapFilterTest extends TestCase {

    private FlashMapFilter filter = null;

    public void setUp() throws Exception {
        super.setUp();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        filter = new FlashMapFilter();
        filter.init(new MockFilterConfig());
    }

    public void testDoFilter() throws Exception {
        ServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FlashMap.put("testKey", "testValue");

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("testValue", request.getAttribute("testKey"));
    }
}
