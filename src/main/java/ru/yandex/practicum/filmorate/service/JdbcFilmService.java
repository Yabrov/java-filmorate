package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

public class JdbcFilmService implements AbstractFilmService {

    @Override
    public Film getFilmById(Integer filmId) {
        return null;
    }

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Iterable<Film> getAllFilms() {
        return null;
    }

    @Override
    public Film addLike(Integer userId, Integer filmId) {
        return null;
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {
        return null;
    }

    @Override
    public Iterable<Film> getMostPopularFilms(int count) {
        return null;
    }
}
