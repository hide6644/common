package common.webapp.converter.util;

import java.io.IOException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.springframework.web.multipart.MultipartFile;

/**
 * XMLファイルを解析するクラス.
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
     *             {@link JAXBException}
     * @throws IOException
     *             {@link IOException}
     */
    public Object read(JAXBContext context, MultipartFile multipartFile) throws JAXBException, IOException {
        return context.createUnmarshaller().unmarshal(multipartFile.getInputStream());
    }
}
