package common.webapp.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Localeをユーザが選べるようにするWrapperクラス.
 */
public class LocaleRequestWrapper extends HttpServletRequestWrapper {

    /** 優先Locale */
    private final Locale preferredLocale;

    /**
     * Localeを設定する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param userLocale
     *            {@link Locale}
     */
    public LocaleRequestWrapper(HttpServletRequest request, Locale userLocale) {
        super(request);
        preferredLocale = userLocale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        if (preferredLocale != null) {
            return preferredLocale;
        } else {
            return super.getLocale();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<Locale> getLocales() {
        if (preferredLocale != null) {
            List<Locale> l = Collections.list(super.getLocales());

            if (l.contains(preferredLocale)) {
                l.remove(preferredLocale);
            }

            l.add(0, preferredLocale);
            return Collections.enumeration(l);
        } else {
            return super.getLocales();
        }
    }
}
