package common.webapp.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Localeをユーザが選べるようにするWrapperクラス.
 */
public class LocaleRequestWrapper extends HttpServletRequestWrapper {

    private final Locale preferredLocale;

    /**
     * Localeを設定する.
     *
     * @param decorated
     *            {@link HttpServletRequest}
     * @param userLocale
     *            {@link Locale}
     */
    public LocaleRequestWrapper(HttpServletRequest decorated, Locale userLocale) {
        super(decorated);
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
