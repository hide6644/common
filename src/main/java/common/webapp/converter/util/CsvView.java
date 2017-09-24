package common.webapp.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import common.Constants;

/**
 * CSVファイルを作成するクラス.
 */
public class CsvView extends AbstractUrlBasedView {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String[]> csv = null;

        if (getUrl() != null) {
            CSVReader reader = new CSVReader(new InputStreamReader(getTemplateSource(getUrl(), request), Constants.ENCODING), ',', '"');

            try {
                csv = reader.readAll();
            } finally {
                IOUtils.closeQuietly(reader);
            }
        } else {
            csv = new ArrayList<>();
        }

        buildCsvDocument(model, csv);
        doRender(csv, response);
    }

    /**
     * CSVファイルを出力する.
     *
     * @param csv
     *            CSVファイルオブジェクト
     * @param response
     *            {@link HttpServletResponse}
     * @throws IOException
     *             {@link IOException}
     */
    private void doRender(List<String[]> csv, HttpServletResponse response) throws IOException {
        CSVWriter writer = new CSVWriter(new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)));
        writer.writeAll(csv);
        IOUtils.closeQuietly(writer);
    }

    /**
     * 既存のCSVファイルから作成する.
     *
     * @param url
     *            拡張子なしのCSVテンプレートのURL
     * @param request
     *            {@link HttpServletRequest}
     * @return CSVファイル
     * @throws IOException
     *             {@link IOException}
     */
    protected InputStream getTemplateSource(String url, HttpServletRequest request) throws IOException {
        LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
        Locale userLocale = RequestContextUtils.getLocale(request);
        Resource inputFile = helper.findLocalizedResource(url.substring(0, url.lastIndexOf('.')), url.substring(url.lastIndexOf('.')), userLocale);
        return inputFile.getInputStream();
    }

    /**
     * CSVドキュメントを作成する.
     *
     * @param model
     *            出力用Map
     * @param csv
     *            CSVファイルオブジェクト
     */
    @SuppressWarnings("unchecked")
    protected void buildCsvDocument(Map<String, Object> model, List<String[]> csv) {
        int rowCount = csv.size();
        String[] template = csv.get(rowCount - 1);

        Grid grid = new Grid((List<?>) model.get("csv"), template);
        csv.remove(rowCount - 1);
        csv.addAll(grid.toStringArray((Set<Formatter<?>>) model.get("formatters")));
    }
}
