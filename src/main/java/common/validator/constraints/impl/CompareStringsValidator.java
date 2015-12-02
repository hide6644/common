package common.validator.constraints.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

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
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(propertyNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }
}
