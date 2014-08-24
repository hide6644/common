package common.validator.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import common.validator.constraints.NotEmptyFile;

/**
 * アップロードファイルの存在をチェックする実装クラス.
 *
 * @author hide6644
 */
public class NotEmptyFileValidator implements ConstraintValidator<NotEmptyFile, Object> {

    /*
     * (非 Javadoc)
     *
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    public void initialize(NotEmptyFile constraintAnnotation) {
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
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
