package common.model;

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
    private String label;

    /** 値 */
    private String value;

    /**
     * デフォルト・コンストラクタ
     */
    public LabelValue() {
    }

    /**
     * コンストラクタ
     *
     * @param label
     *            ラベル
     * @param value
     *            値
     */
    public LabelValue(String label, String value) {
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
     * ラベルを設定する.
     *
     * @param label
     *            ラベル
     */
    public void setLabel(String label) {
        this.label = label;
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
     * 値を設定する.
     *
     * @param value
     *            値
     */
    public void setValue(String value) {
        this.value = value;
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
