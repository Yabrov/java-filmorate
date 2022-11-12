package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(Film film) {
        super("Film with id=" + film.getId() + " doesn't exist.");
    }
}
