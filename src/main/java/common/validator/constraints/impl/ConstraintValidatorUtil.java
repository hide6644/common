package common.validator.constraints.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public static boolean isValid(List<String> propertyValues, ComparisonMode comparisonMode) {
        boolean ignoreCase = false;

        switch (comparisonMode) {
        case EQUAL_IGNORE_CASE:
            ignoreCase = true;
        case EQUAL:
            return changePropertyValues(propertyValues, ignoreCase).size() == 1;
        case NOT_EQUAL_IGNORE_CASE:
            ignoreCase = true;
        case NOT_EQUAL:
            return changePropertyValues(propertyValues, ignoreCase).size() == propertyValues.size();
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
    private static Set<String> changePropertyValues(List<String> propertyValues, boolean ignoreCase) {
        return propertyValues.stream().map(propertyValue -> ignoreCase ? propertyValue.toLowerCase() : propertyValue).collect(Collectors.toSet());
    }
}
