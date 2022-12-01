package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryFilmRepository implements AbstractRepository<Film> {

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer nextId = 1;

    @Override
    public Film save(Film film) {
        Film persistedFilm = film.withId(getNextId());
        log.info("{} created.", persistedFilm);
        films.put(persistedFilm.getId(), persistedFilm);
        return persistedFilm;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film);
        }
        log.info("Film with id={} has been updated.", film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        return films.remove(film.getId());
    }

    @Override
    public Film findById(Integer id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
