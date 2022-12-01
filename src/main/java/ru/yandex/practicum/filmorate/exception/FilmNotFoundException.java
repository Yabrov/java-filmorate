package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class FilmNotFoundException extends RuntimeException {

    private final static String MES_FORMAT = "Film with id=%s does not exist.";

    private final Film film;

    public FilmNotFoundException(Film film) {
        super(String.format(MES_FORMAT, film.getId()));
        this.film = film;
    }

    public FilmNotFoundException(Integer filmId) {
        super(String.format(MES_FORMAT, filmId));
        this.film = null;
    }
}
