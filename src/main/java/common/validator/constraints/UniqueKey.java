package common.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import common.validator.constraints.impl.UniqueKeyValidator;

/**
 * ユニークか確認する.
 */
@Documented
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueKeyValidator.class })
public @interface UniqueKey {

    /** @return 確認対象の列名 */
    String[] columnNames();

    String message() default "{common.validator.constraints.UniqueKey.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target({ TYPE })
    @Retention(RUNTIME)
    @interface List {
        UniqueKey[] value();
    }
}
