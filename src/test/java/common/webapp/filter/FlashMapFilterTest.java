package common.webapp.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import javax.servlet.ServletRequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class FlashMapFilterTest {

    private static FlashMapFilter filter;

    @BeforeAll
    static void setUpClass() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        filter = new FlashMapFilter();
        filter.init(new MockFilterConfig());
    }

    @Test
    void testDoFilter() throws Exception {
        ServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FlashMap.put("testKey", Arrays.asList("testValue"));

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(Arrays.asList("testValue"), request.getAttribute("testKey"));
    }
}
