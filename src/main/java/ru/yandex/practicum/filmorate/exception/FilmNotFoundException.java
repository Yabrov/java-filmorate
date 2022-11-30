package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class FilmNotFoundException extends RuntimeException {

    private final Film film;

    public FilmNotFoundException(Film film) {
        super("Film with id=" + film.getId() + " doesn't exist.");
        this.film = film;
    }

    public FilmNotFoundException(Integer filmId) {
        super("Film with id=" + filmId + " doesn't exist.");
        this.film = null;
    }
}
