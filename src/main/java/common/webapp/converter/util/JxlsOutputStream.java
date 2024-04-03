package common.webapp.converter.util;

import java.io.IOException;
import java.io.OutputStream;

import org.jxls.builder.JxlsOutput;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Excelを書き込むためのOutputStreamを提供するクラス.
 */
@RequiredArgsConstructor
public class JxlsOutputStream implements JxlsOutput {

    /** ページング情報 */
    @NonNull
    private OutputStream os;

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return os;
    }
}
