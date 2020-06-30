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

@Length(max = 255, message = "Слишком длинное название!")
@Pattern(regexp = "^[a-zA-Zа-яА-Я0-9\\s_\\-`'\"*+()!]*$", message = "Содержит недопустимые символы!")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface BasicNamesConstraint {
    String message() default "Название задано некорректно!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
