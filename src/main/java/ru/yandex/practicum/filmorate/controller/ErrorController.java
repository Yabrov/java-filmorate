package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmReleaseDateException;

@ControllerAdvice
public class ErrorController {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String genericExceptionHandler(Exception e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validationExceptionHandler(MethodArgumentNotValidException e) {
        FieldError error = e.getFieldErrors().get(0);
        return "Validation exception [class: '" + error.getObjectName()
                + "', field: '" + error.getField() + "', reason: '"
                + error.getDefaultMessage() + "']";
    }

    @ResponseBody
    @ExceptionHandler(WrongFilmReleaseDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String wrongFilmReleaseDate(WrongFilmReleaseDateException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String filmNotFound(FilmNotFoundException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFound(UserNotFoundException e) {
        return e.getMessage();
    }
}
