package common.webapp.converter.util;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.ReaderConfig;
import org.jxls.reader.XLSReadStatus;
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
        try (var templateInputStream = templateFile.getInputStream();
                var excelInputStream = excelFile.getInputStream()) {
            ReaderConfig.getInstance().setSkipErrors(true);

            return ReaderBuilder.buildFromXML(templateInputStream).read(excelInputStream, model);
        }
    }
}
