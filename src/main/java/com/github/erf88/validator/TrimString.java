package com.github.erf88.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = { TrimStringValidator.class })
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrimString {

    String message() default "field cannot have black spaces at the beginning or at end";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
