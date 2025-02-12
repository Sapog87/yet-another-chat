package ru.sber.yetanotherchat.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Positive
@Negative
@ConstraintComposition(CompositionType.OR)
public @interface NotZero {
    String message() default "Is zero";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
