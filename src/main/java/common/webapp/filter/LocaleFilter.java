package common.webapp.filter;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import common.Constants;

/**
 * Localeをユーザが選べるようにするWrapperクラスをFilterChainに設定するクラス.
 */
public class LocaleFilter extends OncePerRequestFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String locale = request.getParameter("locale");
        var preferredLocale = getPreferredLocale(locale);
        HttpSession session = request.getSession(locale != null);

        if (session != null) {
            if (preferredLocale == null) {
                preferredLocale = (Locale) session.getAttribute(Constants.PREFERRED_LOCALE_KEY);
            } else {
                session.setAttribute(Constants.PREFERRED_LOCALE_KEY, preferredLocale);
            }
        }

        if (preferredLocale != null && !(request instanceof LocaleRequestWrapper)) {
            request = new LocaleRequestWrapper(request, preferredLocale);
            LocaleContextHolder.setLocale(preferredLocale);
        }

        chain.doFilter(request, response);

        LocaleContextHolder.setLocaleContext(null);
    }

    /**
     * 地域を表す文字列からLocalオブジェクトを生成する.
     *
     * @param locale
     *            地域を表す文字列
     * @return 地域
     */
    private Locale getPreferredLocale(String locale) {
        Locale preferredLocale = null;

        if (locale != null) {
            int indexOfUnderscore = locale.indexOf('_');

            if (indexOfUnderscore != -1) {
                var language = locale.substring(0, indexOfUnderscore);
                var country = locale.substring(indexOfUnderscore + 1);
                preferredLocale = Locale.of(language, country);
            } else {
                preferredLocale = Locale.of(locale);
            }
        }

        return preferredLocale;
    }
}
