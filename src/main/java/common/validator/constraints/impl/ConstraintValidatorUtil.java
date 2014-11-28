package common.validator.constraints.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.validator.constraints.ComparisonMode;

/**
 * 値の一致、不一致を判定するUtilityクラス.
 */
public class ConstraintValidatorUtil {

    /**
     * プライベート・コンストラクタ.
     * Utilityクラスはインスタンス化禁止.
     */
    private ConstraintValidatorUtil() {
    }

    /**
     * 判定する.
     *
     * @param propertyValues
     *            比較を行うプロパティ名
     * @param comparisonMode
     *            比較モード
     * @return 比較結果
     */
    public static boolean isValid(Collection<String> propertyValues, ComparisonMode comparisonMode) {
        boolean ignoreCase = false;

        switch (comparisonMode) {
        case EQUAL_IGNORE_CASE:
            ignoreCase = true;
        case EQUAL:
            Set<String> uniqueValues = new HashSet<String>(changePropertyValues(propertyValues, ignoreCase));
            return uniqueValues.size() == 1 ? true : false;
        case NOT_EQUAL_IGNORE_CASE:
            ignoreCase = true;
        case NOT_EQUAL:
            Set<String> allValues = new HashSet<String>(changePropertyValues(propertyValues, ignoreCase));
            return allValues.size() == propertyValues.size() ? true : false;
        }

        return true;
    }

    /**
     * 大文字小文字を区別するかによって、比較を行うプロパティ名を変換する.
     *
     * @param propertyValues
     *            比較を行うプロパティ名
     * @param ignoreCase
     *            true:大文字小文字を区別しない、false:大文字小文字を区別する
     * @return 比較を行うプロパティ名
     */
    private static List<String> changePropertyValues(Collection<String> propertyValues, boolean ignoreCase) {
        List<String> values = new ArrayList<String>(propertyValues.size());

        for (String propertyValue : propertyValues) {
            if (ignoreCase) {
                values.add(propertyValue.toLowerCase());
            } else {
                values.add(propertyValue);
            }
        }

        return values;
    }
}
