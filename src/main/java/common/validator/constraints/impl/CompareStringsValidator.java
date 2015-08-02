package common.validator.constraints.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import common.Constants;
import common.validator.constraints.CompareStrings;
import common.validator.constraints.ComparisonMode;

/**
 * 入力値の一致、不一致をチェックする実装クラス.
 */
public class CompareStringsValidator implements ConstraintValidator<CompareStrings, Object> {

    /** 比較対象のプロパティ名 */
    private String[] propertyNames;

    /** 比較モード */
    private ComparisonMode comparisonMode;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(CompareStrings constraintAnnotation) {
        propertyNames = constraintAnnotation.propertyNames();
        comparisonMode = constraintAnnotation.comparisonMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        List<String> propertyValues = new ArrayList<String>(propertyNames.length);
        ConfigurablePropertyAccessor fieldAccessor = PropertyAccessorFactory.forDirectFieldAccess(target);

        for (int i = 0; i < propertyNames.length; i++) {
            propertyValues.add((String) fieldAccessor.getPropertyValue(propertyNames[i]));
        }

        boolean isValid = ConstraintValidatorUtil.isValid(propertyValues, comparisonMode);

        if (!isValid) {
            String message = resolveMessage(context.getDefaultConstraintMessageTemplate(), target, propertyNames);

            context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }

    /**
     * エラーメッセージにフィールド名を連結する.
     *
     * @param message
     *            エラーメッセージ
     * @param target
     *            チェックしたクラス
     * @param propertyNames
     *            チェックしたフィールド名のリスト
     * @return 連結したエラーメッセージ
     */
    private String resolveMessage(String message, Object target, String[] propertyNames) {
        String className = target.getClass().getSimpleName().toLowerCase();
        List<String> propertyNameList = new ArrayList<String>();

        for (String propertyName : propertyNames) {
            propertyNameList.add(ResourceBundle.getBundle(Constants.BUNDLE_KEY).getString(className + "." + propertyName));
        }

        return message + concatPropertyNames(propertyNameList);
    }

    /**
     * フィールド名を連結する.
     *
     * @param propertyNames
     *            フィールド名のリスト
     * @return 連結したフィールド名
     */
    private StringBuilder concatPropertyNames(List<String> propertyNames) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');

        for (String propertyName : propertyNames) {
            builder.append(propertyName);
            builder.append(", ");
        }

        builder.delete(builder.length() - 2, builder.length());
        builder.append(')');

        return builder;
    }
}
