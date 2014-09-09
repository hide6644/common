package common.webapp.converter.util;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFWorkbook workbook = createWorkbook(request);
        buildExcelDocument(model, workbook);
        doRender(workbook, response);
    }

    /**
     * Excelファイルオブジェクトを作成する.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return Excelファイルオブジェクト
     * @throws IOException
     *             {@link IOException}
     */
    private HSSFWorkbook createWorkbook(HttpServletRequest request) throws IOException {
        if (getUrl() != null) {
            return getTemplateSource(getUrl(), request);
        } else {
            return new HSSFWorkbook();
        }
    }

    /**
     * Excelファイルを出力する.
     *
     * @param workbook
     *            Excelファイルオブジェクト
     * @param response
     *            {@link HttpServletResponse}
     * @throws IOException
     *             {@link IOException}
     */
    private void doRender(HSSFWorkbook workbook, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
    }

    /**
     * 既存のXLS文書からブックを作成する.
     *
     * @param url
     *            拡張子なしのExcelテンプレートのURL
     * @param request
     *            {@link HttpServletRequest}
     * @return Excelファイルオブジェクト
     * @throws IOException
     *             {@link IOException}
     */
    protected HSSFWorkbook getTemplateSource(String url, HttpServletRequest request) throws IOException {
        LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
        Locale userLocale = RequestContextUtils.getLocale(request);
        Resource inputFile = helper.findLocalizedResource(url.endsWith(EXTENSION) ? url.substring(0, url.length() - EXTENSION.length()) : url, EXTENSION, userLocale);

        return new HSSFWorkbook(new POIFSFileSystem(inputFile.getInputStream()));
    }

    /**
     * Excelドキュメントを作成する.
     *
     * @param model
     *            出力用Map
     * @param workbook
     *            Excelファイルオブジェクト
     */
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook) {
        new XLSTransformer().transformWorkbook(workbook, model);
    }
}
