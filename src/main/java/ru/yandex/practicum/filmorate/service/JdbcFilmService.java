package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcFilmService implements AbstractFilmService {

    private final AbstractRepository<Integer, Film> jdbcFilmRepository;

    @Override
    public Film getFilmById(Integer filmId) {
        Film film = jdbcFilmRepository.findById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId);
        }
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        return jdbcFilmRepository.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new FilmNotFoundException(film);
        }
        return jdbcFilmRepository.update(film);
    }

    @Override
    public Iterable<Film> getAllFilms() {
        return jdbcFilmRepository.findAll();
    }

    @Override
    public Film addLike(Integer userId, Integer filmId) {
        // TODO: 04.01.2023
        return null;
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {
        // TODO: 04.01.2023
        return null;
    }

    @Override
    public Iterable<Film> getMostPopularFilms(int count) {
        return jdbcFilmRepository.findFirstNTopRows(count);
    }
}
