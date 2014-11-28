package common.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * LabelとValueの保持するクラス.
 */
public class LabelValue implements Comparable<LabelValue>, Serializable {

    public static final Comparator<LabelValue> CASE_INSENSITIVE_ORDER = new Comparator<LabelValue>() {
        /**
         * {@inheritDoc}
         */
        public int compare(LabelValue labelValue1, LabelValue labelValue2) {
            return labelValue1.getLabel().compareToIgnoreCase(labelValue2.getLabel());
        }
    };

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
        return label.compareTo(labelValue.getLabel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LabelValue)) {
            return false;
        }

        LabelValue labelValue = (LabelValue) o;

        return value != null ? value.equals(labelValue.value) : labelValue.value == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
