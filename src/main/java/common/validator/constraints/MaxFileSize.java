package common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import common.validator.constraints.impl.MaxFileSizeValidator;

/**
 * アップロードファイルの容量をチェックする.
 */
@Documented
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Constraint(validatedBy = MaxFileSizeValidator.class)
public @interface MaxFileSize {

    /** @return 最大値 */
    int max();

    /** @return 単位 */
    String unitSign() default "M";

    String message() default "{common.validator.constraints.MaxFileSize.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
