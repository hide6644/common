package common.webapp.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

/**
 * ユーザが入力した数値文字列を解析してBeanのDateTimeプロパティに変換するクラス.
 */
public class LocalDateTimeEditor extends PropertyEditorSupport {

    /** 日付/時間オブジェクトの出力および解析のためのフォーマッタ */
    private final DateTimeFormatter dateTimeFormat;

    /** 空文字を許可 */
    private final boolean allowEmpty;

    /**
     * コンストラクタ
     *
     * @param dateTimeFormat
     *            日付/時間オブジェクトの出力および解析のためのフォーマッタ
     * @param allowEmpty
     *            空文字を許可
     */
    public LocalDateTimeEditor(DateTimeFormatter dateTimeFormat, boolean allowEmpty) {
        this.dateTimeFormat = dateTimeFormat;
        this.allowEmpty = allowEmpty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsText() {
        LocalDateTime datetime = (LocalDateTime) getValue();

        try {
            return datetime != null ? datetime.format(dateTimeFormat) : "";
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Could not format date: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsText(String text) {
        if (allowEmpty && !StringUtils.hasText(text)) {
            setValue(null);
        } else {
            try {
                setValue(LocalDateTime.parse(text, dateTimeFormat));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Could not parse date: " + e.getMessage(), e);
            }
        }
    }
}
