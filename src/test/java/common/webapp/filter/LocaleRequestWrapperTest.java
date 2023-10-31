package common.webapp.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class LocaleRequestWrapperTest {

    @Test
    void testSetLocaleInSessionWhenSessionIsNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("locale", "en");

        LocaleRequestWrapper wrapper = new LocaleRequestWrapper(request, null);

        assertNotNull(wrapper.getLocale());
        assertNotNull(wrapper.getLocales());

        wrapper = new LocaleRequestWrapper(request, Locale.of("jp"));

        assertNotNull(wrapper.getLocale());
        assertNotNull(wrapper.getLocales());
    }
}
