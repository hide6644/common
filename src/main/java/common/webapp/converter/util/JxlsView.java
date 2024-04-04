package common.webapp.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jxls.builder.JxlsStreaming;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Excelファイルを作成するクラス.
 */
public class JxlsView extends AbstractUrlBasedView {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (InputStream is = getTemplateSource(getUrl(), request);
                OutputStream os = response.getOutputStream()) {
            JxlsPoiTemplateFillerBuilder.newInstance()
                .withStreaming(JxlsStreaming.STREAMING_ON)
                .withTemplate(is)
                .buildAndFill(model, new JxlsOutputStream(os));
        }
    }

    /**
     * テンプレートのXLS文書を読み込む.
     *
     * @param url
     *            拡張子なしのExcelテンプレートのURL
     * @param request
     *            {@link HttpServletRequest}
     * @return Excelファイル
     * @throws IOException
     *             {@link IOException}
     */
    private InputStream getTemplateSource(String url, HttpServletRequest request) throws IOException {
        if (url == null) {
            throw new NullPointerException("Property 'url' is required");
        }

        var helper = new LocalizedResourceHelper(getApplicationContext());
        var userLocale = RequestContextUtils.getLocale(request);
        var inputFile = helper.findLocalizedResource(url.substring(0, url.lastIndexOf('.')), url.substring(url.lastIndexOf('.')), userLocale);
        return inputFile.getInputStream();
    }
}
