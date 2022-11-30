package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer nextId = 1;

    @Override
    public Film persistFilm(Film film) {
        Film persistedFilm = film.withId(getNextId());
        log.info("{} created.", persistedFilm);
        films.put(persistedFilm.getId(), persistedFilm);
        return persistedFilm;
    }

    @Override
    public Film replaceFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film);
        }
        log.info("Фильм с id={} успешно обновлен.", film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        return films.remove(film.getId());
    }

    @Override
    public Film getFilm(Integer filmId) {
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
