package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class WrongFilmReleaseDate extends RuntimeException {

    private final Film film;

    public WrongFilmReleaseDate(Film film) {
        super("Release date " + film.getReleaseDateString() + " is wrong.");
        this.film = film;
    }
}
