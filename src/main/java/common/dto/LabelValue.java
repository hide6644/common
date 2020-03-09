package common.dto;

import java.io.Serializable;
import java.util.Comparator;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * LabelとValueの保持するクラス.
 */
@Value(staticConstructor = "of")
public final class LabelValue implements Comparable<LabelValue>, Serializable {

    public static final Comparator<LabelValue> CASE_INSENSITIVE_ORDER = (LabelValue o1, LabelValue o2) -> o1.getValue().compareToIgnoreCase(o2.getValue());

    /** ラベル */
    @EqualsAndHashCode.Exclude
    private final String label;

    /** 値 */
    private final String value;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(LabelValue labelValue) {
        return value.compareTo(labelValue.getValue());
    }
}
