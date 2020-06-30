package com.vakhnenko.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@DecimalMin(value = "0.03", message = "Слишком маленькая продолжительность!")
@DecimalMax(value = "1440.0", message = "Слишком большая продолжительность!")
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface LengthConstraint {
    String message() default "Продолжительность песни задана некорректно!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
