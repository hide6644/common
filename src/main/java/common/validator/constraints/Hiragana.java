package common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

/**
 * 入力値が平仮名かチェックする.
 */
@Documented
@Constraint(validatedBy = {})
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = "[\\p{InHiragana}]*")
public @interface Hiragana {

    String message() default "{common.validator.constraints.Hiragana.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
