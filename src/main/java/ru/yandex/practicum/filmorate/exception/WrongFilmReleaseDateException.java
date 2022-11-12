package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class WrongFilmReleaseDateException extends RuntimeException {

    public WrongFilmReleaseDateException(Film film) {
        super("Release date " + film.getReleaseDateString() + " is wrong.");
    }
}
