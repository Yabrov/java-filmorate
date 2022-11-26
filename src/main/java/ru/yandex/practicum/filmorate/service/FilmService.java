package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer nextId = 1;

    public Film createFilm(Film film) {
        Film newFilm = film.withId(nextId++);
        log.info("{} created.", newFilm);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film);
        }
        log.info("Фильм с id={} успешно обновлен.", film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    public Iterable<Film> getAllFilms() {
        return films.values();
    }
}
