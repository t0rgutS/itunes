package com.vakhnenko.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.PastOrPresent;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@PastOrPresent(message = "Дата релиза альбома указана некорректно!")
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface DateConstraint {
    String message() default "Дата указана неверно!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
