package common.webapp.converter.util;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.web.multipart.MultipartFile;

/**
 * XMLファイルを解析するクラス.
 *
 * @author hide6644
 */
public class XmlFileReader {

    /**
     * XMLファイルから値を抽出する.
     *
     * @param context
     *            JAXB API のエントリポイント
     * @param multipartFile
     *            アップロードファイル
     * @return XMLファイル解析結果
     * @throws JAXBException
     * @throws IOException
     */
    public Object read(JAXBContext context, MultipartFile multipartFile) throws JAXBException, IOException {
        return context.createUnmarshaller().unmarshal(multipartFile.getInputStream());
    }
}
