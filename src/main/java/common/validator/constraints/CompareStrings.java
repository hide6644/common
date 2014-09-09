package common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import common.validator.constraints.impl.CompareStringsValidator;

/**
 * 入力値の一致、不一致をチェックする.
 */
@Documented
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CompareStringsValidator.class)
public @interface CompareStrings {

    String[] propertyNames();

    ComparisonMode comparisonMode();

    String message() default "{common.validator.constraints.Equal.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target( { TYPE  })
    @Retention(RUNTIME)
    @interface List {
        CompareStrings[] value();
    }
}
