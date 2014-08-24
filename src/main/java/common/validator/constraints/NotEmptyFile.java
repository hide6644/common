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
 *
 * @author hide6644
 */
@Documented
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Constraint(validatedBy = NotEmptyFileValidator.class)
public @interface NotEmptyFile {

    String message() default "{common.validator.constraints.NotEmptyFile.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target( { TYPE  })
    @Retention(RUNTIME)
    @interface List {
        NotEmptyFile[] value();
    }
}
