package common.webapp.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.ReaderConfig;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

/**
 * エクセルファイルを解析するクラス.
 */
public class JxlsFileReader {

    /**
     * XLSファイルから値を抽出する.
     *
     * @param templateFile
     *            エクセルファイル解析に使用するXMLファイル
     * @param excelFile
     *            エクセルファイル
     * @param model
     *            抽出した値を設定するオブジェクトを格納したMap
     * @return エクセルファイル解析結果
     * @throws InvalidFormatException
     *             {@link InvalidFormatException}
     * @throws IOException
     *             {@link IOException}
     * @throws SAXException
     *             {@link SAXException}
     */
    public XLSReadStatus read(Resource templateFile, MultipartFile excelFile, Map<?, ?> model) throws InvalidFormatException, IOException, SAXException {
        InputStream templateInputStream = null;
        InputStream excelInputStream = null;

        try {
            ReaderConfig.getInstance().setSkipErrors(true);
            templateInputStream = templateFile.getInputStream();
            XLSReader mainReader = ReaderBuilder.buildFromXML(templateInputStream);

            excelInputStream = excelFile.getInputStream();

            return mainReader.read(excelInputStream, model);
        } finally {
            IOUtils.closeQuietly(templateInputStream);
            IOUtils.closeQuietly(excelInputStream);
        }
    }
}
