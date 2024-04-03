package common.webapp.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.util.StringUtils;
import lombok.AllArgsConstructor;

/**
 * ユーザが入力した数値文字列を解析してBeanのDateTimeプロパティに変換するクラス.
 */
@AllArgsConstructor
public class LocalDateTimeEditor extends PropertyEditorSupport {

    /** 日付/時間オブジェクトの出力および解析のためのフォーマッタ */
    private final DateTimeFormatter dateTimeFormat;

    /** 空文字を許可 */
    private final boolean allowEmpty;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsText() {
        try {
            return Optional.ofNullable((LocalDateTime) getValue()).map(datetime -> datetime.format(dateTimeFormat)).orElse("");
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
