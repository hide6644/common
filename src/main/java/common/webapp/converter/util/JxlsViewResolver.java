package common.webapp.converter.util;

import java.util.Locale;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * View名に「jxls/」が含まれていた場合、ViewResolverにJxlsViewを使用するように設定するクラス.
 */
public class JxlsViewResolver extends UrlBasedViewResolver {

    /**
     * デフォルト・コンストラクタ.
     */
    public JxlsViewResolver() {
        setViewClass(JxlsView.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<JxlsView> requiredViewClass() {
        return JxlsView.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canHandle(String viewName, Locale locale) {
        return viewName.indexOf("jxls/") > -1 && super.canHandle(viewName, locale);
    }
}
