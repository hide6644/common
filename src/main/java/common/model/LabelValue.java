package common.model;

import java.io.Serializable;
import java.util.Comparator;

public class LabelValue implements Comparable<LabelValue>, Serializable {

    public static final Comparator<LabelValue> CASE_INSENSITIVE_ORDER = new Comparator<LabelValue>() {
        /**
         * {@inheritDoc}
         */
        public int compare(LabelValue labelValue1, LabelValue labelValue2) {
            return labelValue1.getLabel().compareToIgnoreCase(labelValue2.getLabel());
        }
    };

    private String label;

    private String value;

    public LabelValue() {
    }

    public LabelValue(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(LabelValue labelValue) {
        return label.compareTo(labelValue.getLabel());
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LabelValue)) {
            return false;
        }

        final LabelValue labelValue = (LabelValue) o;

        return !(value != null ? !value.equals(labelValue.value) : labelValue.value != null);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }
}
