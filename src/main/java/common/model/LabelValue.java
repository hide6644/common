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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LabelValue other = (LabelValue) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
