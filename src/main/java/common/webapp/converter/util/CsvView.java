package common.webapp.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

import common.Constants;
import common.entity.User;

/**
 * CSVファイルを作成するクラス.
 */
public class CsvView extends AbstractUrlBasedView {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException, CsvException {
        try (InputStream is = getTemplateSource(getUrl(), request);
                InputStreamReader isr = new InputStreamReader(is, Constants.ENCODING);
                CSVReader reader = new CSVReaderBuilder(isr).build()) {
            List<String[]> csv = reader.readAll();

            try (OutputStreamWriter os = new OutputStreamWriter(response.getOutputStream(), Constants.ENCODING);
                    CSVWriter writer = new CSVWriter(os)) {
                // ヘッダー行を追加
                writer.writeNext(csv.get(0));

                ColumnPositionMappingStrategy<User> strat = new ColumnPositionMappingStrategy<>();
                strat.setType(User.class);
                strat.setColumnMapping(csv.get(1));

                StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(writer)
                        .withMappingStrategy(strat)
                        .build();
                beanToCsv.write((List<User>) model.get("csv"));
            }
        }
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
    private InputStream getTemplateSource(String url, HttpServletRequest request) throws IOException {
        LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
        Locale userLocale = RequestContextUtils.getLocale(request);
        Resource inputFile = helper.findLocalizedResource(url.substring(0, url.lastIndexOf('.')), url.substring(url.lastIndexOf('.')), userLocale);
        return inputFile.getInputStream();
    }
}
