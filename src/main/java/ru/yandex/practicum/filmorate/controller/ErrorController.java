package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.exception.WrongFilmReleaseDate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@ControllerAdvice
public class ErrorController {

    @ResponseBody
    @ExceptionHandler(WrongFilmReleaseDate.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Film wrongFilmReleaseDate(WrongFilmReleaseDate e) {
        return e.getFilm();
    }

    @ResponseBody
    @ExceptionHandler(FilmNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Film filmNotFound(FilmNotFound e) {
        return e.getFilm();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public User userNotFound(UserNotFound e) {
        return e.getUser();
    }
}
