package common.validator.constraints.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import common.validator.constraints.MaxFileSize;

/**
 * アップロードファイルの容量をチェックする実装クラス.
 */
public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, Object> {

    /** 最大容量 */
    private int max;

    /** 倍数 */
    private int multiple;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        if (constraintAnnotation.max() < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }

        max = constraintAnnotation.max();

        switch (constraintAnnotation.unitSign()) {
        case M_BYTE:
            multiple = 1024 * 1024;
            break;
        case K_BYTE:
            multiple = 1024;
            break;
        case BYTE:
            multiple = 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        var isValid = true;

        if (target == null) {
            isValid = false;
        } else if (!(target instanceof MultipartFile)) {
            throw new IllegalArgumentException("Object instance must be MultipartFile.class.");
        } else if (((MultipartFile) target).getSize() > max * multiple) {
            isValid = false;
        }

        return isValid;
    }
}
