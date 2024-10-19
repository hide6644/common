package common.validator.constraints.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

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
        // 何もしない
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        if (target != null && !(target instanceof MultipartFile)) {
            throw new IllegalArgumentException("Object instance must be MultipartFile.class.");
        }

        return !(target == null || target instanceof MultipartFile file && file.getSize() == 0);
    }
}
