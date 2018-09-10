package common.dto;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * LabelとValueの保持するクラス.
 */
public final class LabelValue implements Comparable<LabelValue>, Serializable {

    public static final Comparator<LabelValue> CASE_INSENSITIVE_ORDER = (LabelValue o1, LabelValue o2) -> o1.getValue().compareToIgnoreCase(o2.getValue());

    /** ラベル */
    private final String label;

    /** 値 */
    private final String value;

    /**
     * コンストラクタ
     *
     * @param label
     *            ラベル
     * @param value
     *            値
     */
    private LabelValue(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * ラベルを取得する.
     *
     * @return ラベル
     */
    public String getLabel() {
        return label;
    }

    /**
     * 値を取得する.
     *
     * @return 値
     */
    public String getValue() {
        return value;
    }

    /**
     * {@code LabelValue}のインスタンスを取得する.
     *
     * @param label
     *            ラベル
     * @param value
     *            値
     * @return {@code LabelValue}のインスタンス
     */
    public static LabelValue of(String label, String value) {
        return new LabelValue(label, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(LabelValue labelValue) {
        return value.compareTo(labelValue.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof LabelValue)) {
            return false;
        }

        LabelValue castObj = (LabelValue) obj;
        return new EqualsBuilder()
                .append(value, castObj.value)
                .isEquals();
    }
}
