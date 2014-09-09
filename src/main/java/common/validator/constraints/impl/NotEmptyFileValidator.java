package common.validator.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import common.validator.constraints.NotEmptyFile;

/**
 * アップロードファイルの存在をチェックする実装クラス.
 */
public class NotEmptyFileValidator implements ConstraintValidator<NotEmptyFile, Object> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(NotEmptyFile constraintAnnotation) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (target == null) {
            isValid = false;
        } else if (target instanceof CommonsMultipartFile) {
            if (((CommonsMultipartFile) target).getSize() == 0) {
                isValid = false;
            }
        } else {
            throw new IllegalArgumentException("Object instance must be CommonsMultipartFile.class.");
        }

        return isValid;
    }
}
