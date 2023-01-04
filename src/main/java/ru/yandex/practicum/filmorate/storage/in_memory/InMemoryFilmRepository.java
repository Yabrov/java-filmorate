package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryFilmRepository implements AbstractRepository<Integer, Film> {

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
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            throw new FilmNotFoundException(film);
        }
        log.info("Film with id={} has been updated.", film.getId());
        film.getLikedUsers().clear();
        film.getLikedUsers().addAll(oldFilm.getLikedUsers());
        films.replace(film.getId(), oldFilm, film);
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

    @Override
    public Collection<Film> findByIds(Collection<Integer> ids) {
        return ids.stream().map(this::findById).collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findFirstNTopRows(Integer n) {
        return Collections.emptyList();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
