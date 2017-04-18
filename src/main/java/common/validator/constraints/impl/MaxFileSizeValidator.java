package common.validator.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import common.validator.constraints.MaxFileSize;

/**
 * アップロードファイルの容量をチェックする実装クラス.
 */
public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, Object> {

    /** 最大容量 */
    private int max;

    /** アップロードファイルの容量 */
    private int size;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        max = constraintAnnotation.max();

        if (constraintAnnotation.unitSign().equals("")) {
            size = 1;
        } else if (constraintAnnotation.unitSign().equals("K")) {
            size = 1024;
        } else if (constraintAnnotation.unitSign().equals("M")) {
            size = 1024 * 1024;
        } else {
            throw new IllegalArgumentException("The unitSign parameter must be '', 'K' or 'M'.");
        }

        if (max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }
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
            if (((CommonsMultipartFile) target).getSize() / size > max) {
                isValid = false;
            }
        } else {
            throw new IllegalArgumentException("Object instance must be CommonsMultipartFile.class.");
        }

        return isValid;
    }
}
