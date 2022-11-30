package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final static Comparator<Film> filmPopularityComparator = Comparator
            .comparing((Film film) -> film.getLikes().size()).reversed();

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film getFilmById(Integer filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId);
        }
        return film;
    }

    public Film createFilm(Film film) {
        return filmStorage.persistFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new FilmNotFoundException(film);
        }
        return filmStorage.replaceFilm(film);
    }

    public Iterable<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addLike(Integer userId, Integer filmId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        log.info("Пользователь с id={} лайкнул фильм '{}'.", user.getId(), film.getName());
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(Integer userId, Integer filmId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        log.info("Пользователь с id={} убрал лайк с фильма '{}'.", user.getId(), film.getName());
        film.getLikes().remove(userId);
        return film;
    }

    public Iterable<Film> getMostPopularFilms(int count) {
        return filmStorage
                .getAllFilms()
                .stream()
                .sorted(filmPopularityComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
