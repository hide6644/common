package common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import common.validator.constraints.impl.NotEmptyFileValidator;

/**
 * アップロードファイルの存在をチェックする.
 */
@Documented
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Constraint(validatedBy = NotEmptyFileValidator.class)
public @interface NotEmptyFile {

    /**
     * @return エラーメッセージテンプレート
     */
    String message() default "{common.validator.constraints.NotEmptyFile.message}";

    /**
     * @return 制約が属するグループ
     */
    Class<?>[] groups() default {};

    /**
     * @return 制約に関連付けられたペイロード
     */
    Class<? extends Payload>[] payload() default {};
}
