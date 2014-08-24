package common.webapp.converter.util;

import java.util.Locale;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * View名に「jxls/」が含まれていた場合、ViewResolverにJxlsViewを使用するように設定するクラス.
 *
 * @author hide6644
 */
public class JxlsViewResolver extends UrlBasedViewResolver {

    /**
     * デフォルト・コンストラクタ.
     */
    public JxlsViewResolver() {
        setViewClass(JxlsView.class);
    }

    /*
     * (非 Javadoc)
     *
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#requiredViewClass()
     */
    protected Class<JxlsView> requiredViewClass() {
        return JxlsView.class;
    }

    /*
     * (非 Javadoc)
     *
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#canHandle(java.lang.String, java.util.Locale)
     */
    protected boolean canHandle(String viewName, Locale locale) {
        return viewName.indexOf("jxls/") > 0 && super.canHandle(viewName, locale);
    }
}
