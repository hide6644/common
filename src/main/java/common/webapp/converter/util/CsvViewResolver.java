package common.webapp.converter.util;

import java.util.Locale;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * View名に「csv/」が含まれていた場合、ViewResolverにCsvViewを使用するように設定するクラス.
 */
public class CsvViewResolver extends UrlBasedViewResolver {

    /**
     * デフォルト・コンストラクタ.
     */
    public CsvViewResolver() {
        setViewClass(CsvView.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<CsvView> requiredViewClass() {
        return CsvView.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canHandle(String viewName, Locale locale) {
        return viewName.indexOf("csv/") > -1 && super.canHandle(viewName, locale);
    }
}
