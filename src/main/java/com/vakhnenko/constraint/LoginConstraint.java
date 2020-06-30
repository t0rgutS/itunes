package com.vakhnenko.constraint;

import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotEmpty
@Length(min = 5, max = 100)
@Pattern(regexp = "^[a-zA-Z0-9\\-_]*$", message = "Содержит недопустимые символы!")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface LoginConstraint {
    String message() default "Логин должен содержать не меньше 5 символов и не больше 100 символов!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
