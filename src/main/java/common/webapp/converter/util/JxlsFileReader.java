package common.webapp.converter.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.jxls.reader.ReaderBuilder;
import net.sf.jxls.reader.ReaderConfig;
import net.sf.jxls.reader.XLSReadStatus;
import net.sf.jxls.reader.XLSReader;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

/**
 * XLSファイルを解析するクラス.
 */
public class JxlsFileReader {

    /**
     * XLSファイルから値を抽出する.
     *
     * @param templateFile
     *            XLSファイル解析に使用するXMLファイル
     * @param file
     *            XLSファイル
     * @param model
     *            抽出した値を設定するオブジェクトを格納したMap
     * @return XLSファイル解析結果
     * @throws InvalidFormatException
     *             {@link InvalidFormatException}
     * @throws IOException
     *             {@link IOException}
     * @throws SAXException
     *             {@link SAXException}
     */
    public XLSReadStatus read(Resource templateFile, MultipartFile file, Map<?, ?> model) throws InvalidFormatException, IOException, SAXException {
        InputStream inputXML = null;
        InputStream inputXLS = null;

        try {
            ReaderConfig.getInstance().setSkipErrors(true);
            inputXML = new BufferedInputStream(templateFile.getInputStream());
            XLSReader mainReader = ReaderBuilder.buildFromXML(inputXML);

            inputXLS = new BufferedInputStream(file.getInputStream());

            return mainReader.read(inputXLS, model);
        } finally {
            IOUtils.closeQuietly(inputXML);
            IOUtils.closeQuietly(inputXLS);
        }
    }
}
