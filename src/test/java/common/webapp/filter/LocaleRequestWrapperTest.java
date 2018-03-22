package common.webapp.filter;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class LocaleRequestWrapperTest {

    @Test
    public void testSetLocaleInSessionWhenSessionIsNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("locale", "en");

        LocaleRequestWrapper wrapper = new LocaleRequestWrapper(request, null);

        assertNotNull(wrapper.getLocale());
        assertNotNull(wrapper.getLocales());

        wrapper = new LocaleRequestWrapper(request, new Locale("jp"));

        assertNotNull(wrapper.getLocale());
        assertNotNull(wrapper.getLocales());
    }
}
