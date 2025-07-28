package com.dollop.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dollop.app.annotation.impl.TrimValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TrimValidator.class)
public @interface Trimmed {
    String message() default "Field must not be empty or only spaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}