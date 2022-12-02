package common.validator.constraints.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
        ConfigurablePropertyAccessor fieldAccessor = PropertyAccessorFactory.forDirectFieldAccess(target);
        boolean isValid = ConstraintValidatorUtil.isValid(
                Arrays.stream(propertyNames)
                .map(propertyName -> (String) fieldAccessor.getPropertyValue(propertyName))
                .collect(Collectors.toList()), comparisonMode);

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(propertyNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }
}
