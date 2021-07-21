package com.example.demo.rest.annotation;

import com.example.demo.rest.validator.ConfirmUserIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = { ConfirmUserIdValidator.class })
@Documented
public @interface ConfirmUserId {

    String message() default "ユーザーIDが不正です。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
