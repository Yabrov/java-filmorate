package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film persistFilm(Film film);

    Film replaceFilm(Film film);

    Film deleteFilm(Film film);

    Film getFilm(Integer filmId);

    Collection<Film> getAllFilms();
}
