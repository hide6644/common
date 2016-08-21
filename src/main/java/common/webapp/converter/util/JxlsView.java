package common.webapp.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jxls.area.Area;
import org.jxls.builder.AreaBuilder;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.TransformerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Excelファイルを作成するクラス.
 */
public class JxlsView extends AbstractUrlBasedView {

    /** 拡張子 */
    private static final String EXTENSION = ".xls";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = getTemplateSource(getUrl(), request);
            os = response.getOutputStream();

            Transformer transformer = TransformerFactory.createTransformer(is, os);
            ((JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator()).getJexlEngine().setSilent(true);
            AreaBuilder areaBuilder = new XlsCommentAreaBuilder(transformer);
            List<Area> xlsAreaList = areaBuilder.build();
            Area xlsArea = xlsAreaList.get(0);
            Context context = new Context();

            for (Map.Entry<String, Object> mode : model.entrySet()) {
                context.putVar(mode.getKey(), mode.getValue());
            }

            xlsArea.applyAt(new CellRef("Sheet1!A1"), context);
            transformer.write();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
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
        LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
        Locale userLocale = RequestContextUtils.getLocale(request);
        Resource inputFile = helper.findLocalizedResource(url.endsWith(EXTENSION) ? url.substring(0, url.length() - EXTENSION.length()) : url, EXTENSION, userLocale);

        return inputFile.getInputStream();
    }
}
