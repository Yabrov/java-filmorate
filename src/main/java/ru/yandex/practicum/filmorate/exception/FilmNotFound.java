package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class FilmNotFound extends RuntimeException {

    private final Film film;

    public FilmNotFound(Film film) {
        super("Film with id=" + film.getId() + " doesn't exist.");
        this.film = film;
    }
}
