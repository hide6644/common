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
 *
 * @author hide6644
 */
public class CompareStringsValidator implements ConstraintValidator<CompareStrings, Object> {

    /** 比較するプロパティ */
    private String[] propertyNames;

    /** 比較モード */
    private ComparisonMode comparisonMode;

    /*
     * (非 Javadoc)
     *
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    public void initialize(CompareStrings constraintAnnotation) {
        propertyNames = constraintAnnotation.propertyNames();
        comparisonMode = constraintAnnotation.comparisonMode();
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        List<String> propertyValues = new ArrayList<String>(propertyNames.length);
        ConfigurablePropertyAccessor fieldAccessor = PropertyAccessorFactory.forDirectFieldAccess(target);

        for (int i = 0; i < propertyNames.length; i++) {
            propertyValues.add((String) fieldAccessor.getPropertyValue(propertyNames[i]));
        }

        boolean isValid = ConstraintValidatorHelper.isValid(propertyValues, comparisonMode);

        if (!isValid) {
            String message = ConstraintValidatorHelper.resolveMessage(context.getDefaultConstraintMessageTemplate(), target, propertyNames, comparisonMode);

            context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }
}
