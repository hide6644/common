package common.webapp.filter;

import static org.junit.Assert.*;

import java.util.Arrays;

import javax.servlet.ServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FlashMapFilterTest {

    private FlashMapFilter filter = null;

    @Before
    public void setUp() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        filter = new FlashMapFilter();
        filter.init(new MockFilterConfig());
    }

    @Test
    public void testDoFilter() throws Exception {
        ServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FlashMap.put("testKey", Arrays.asList("testValue"));

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(Arrays.asList("testValue"), request.getAttribute("testKey"));
    }
}
