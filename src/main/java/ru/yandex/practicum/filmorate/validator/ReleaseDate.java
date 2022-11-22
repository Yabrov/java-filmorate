package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.impl.ReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDate {

    String message() default "must be after 28.12.1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
