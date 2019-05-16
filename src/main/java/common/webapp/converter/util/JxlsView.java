package common.webapp.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jxls.area.Area;
import org.jxls.builder.AreaBuilder;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (InputStream is = getTemplateSource(getUrl(), request);
                OutputStream os = response.getOutputStream()) {
            Transformer transformer = TransformerFactory.createTransformer(is, os);
            AreaBuilder areaBuilder = new XlsCommentAreaBuilder(transformer);
            List<Area> xlsAreaList = areaBuilder.build();
            Area xlsArea = xlsAreaList.get(0);
            Context context = new Context();

            model.entrySet().forEach(mode -> context.putVar(mode.getKey(), mode.getValue()));

            xlsArea.applyAt(new CellRef("Sheet1!A1"), context);
            transformer.write();
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
        Resource inputFile = helper.findLocalizedResource(url.substring(0, url.lastIndexOf('.')), url.substring(url.lastIndexOf('.')), userLocale);
        return inputFile.getInputStream();
    }
}
