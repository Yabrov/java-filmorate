package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

public interface AbstractFilmService {

    Film getFilmById(Integer filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Iterable<Film> getAllFilms();

    Film addLike(Integer userId, Integer filmId);

    Film removeLike(Integer userId, Integer filmId);

    Iterable<Film> getMostPopularFilms(int count);

    Integer deleteFilm(Film film);
}
