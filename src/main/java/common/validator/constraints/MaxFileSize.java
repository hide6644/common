package common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import common.validator.constraints.impl.MaxFileSizeValidator;

/**
 * アップロードファイルの容量をチェックする.
 */
@Documented
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Constraint(validatedBy = MaxFileSizeValidator.class)
public @interface MaxFileSize {

    /**
     * 最大値
     */
    int max();

    /**
     * 単位
     */
    FileSizeUnitSign unitSign() default FileSizeUnitSign.M_BYTE;

    /**
     * エラーメッセージテンプレート
     */
    String message() default "{common.validator.constraints.MaxFileSize.message}";

    /**
     * @return 制約が属するグループ
     */
    Class<?>[] groups() default {};

    /**
     * @return 制約に関連付けられたペイロード
     */
    Class<? extends Payload>[] payload() default {};
}
