package common.validator.constraints.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import common.Constants;
import common.validator.constraints.ComparisonMode;

public class ConstraintValidatorHelper {

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
     *            true:大文字小文字を区別しない
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

    public static String resolveMessage(String message, Object target, String[] propertyNames, ComparisonMode comparisonMode) {
        String className = target.getClass().getSimpleName().toLowerCase();
        List<String> propertyNameList = new ArrayList<String>();

        for (String propertyName : propertyNames) {
            propertyNameList.add(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString(className + "." + propertyName));
        }

        return concatPropertyNames(propertyNameList).append(message).toString();
    }

    private static StringBuilder concatPropertyNames(List<String> propertyNames) {
        StringBuilder builder = new StringBuilder();
        builder.append('"');

        for (String propertyName : propertyNames) {
            builder.append(propertyName);
            builder.append(", ");
        }

        builder.delete(builder.length() - 2, builder.length());
        builder.append('"');

        return builder;
    }
}
