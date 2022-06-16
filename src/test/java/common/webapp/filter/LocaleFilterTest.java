package common.webapp.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import common.Constants;

class LocaleFilterTest {

    private static LocaleFilter filter;

    @BeforeAll
    static void setUpClass() throws Exception {
        filter = new LocaleFilter();
        filter.init(new MockFilterConfig());
    }

    @Test
    void testSetLocaleInSessionWhenSessionIsNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("locale", "es");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        // no session, should result in not null
        Locale locale = (Locale) request.getSession().getAttribute(Constants.PREFERRED_LOCALE_KEY);

        assertNotNull(locale);
        assertNotNull(LocaleContextHolder.getLocale());
        assertEquals(new Locale("es"), locale);
    }

    @Test
    void testSetLocaleInSessionWhenSessionNotNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("locale", "es");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setSession(new MockHttpSession(null));

        filter.doFilter(request, response, new MockFilterChain());

        // session not null, should result in not null
        Locale locale = (Locale) request.getSession().getAttribute(Constants.PREFERRED_LOCALE_KEY);

        assertNotNull(locale);
        assertNotNull(LocaleContextHolder.getLocale());
        assertEquals(new Locale("es"), locale);
    }

    @Test
    void testSetInvalidLocale() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("locale", "foo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setSession(new MockHttpSession(null));

        filter.doFilter(request, response, new MockFilterChain());

        // a locale will get set regardless - there's no such thing as an invalid one
        assertNotNull(request.getSession().getAttribute(Constants.PREFERRED_LOCALE_KEY));
    }

    @Test
    void testLocaleAndCountry() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        request.addParameter("locale", "zh_TW");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        // session not null, should result in not null
        Locale locale = (Locale) request.getSession().getAttribute(Constants.PREFERRED_LOCALE_KEY);

        assertNotNull(locale);
        assertEquals(new Locale("zh", "TW"), locale);
    }

    @Test
    void testNullLocale() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setSession(new MockHttpSession(null));

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(request.getSession().getAttribute(Constants.PREFERRED_LOCALE_KEY));
    }
}
