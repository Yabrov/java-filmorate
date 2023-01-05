package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcFilmService implements AbstractFilmService {

    private final AbstractRepository<Integer, Film> jdbcFilmRepository;
    private final AbstractRepository<Likes, Likes> jdbcLikesRepository;
    private final AbstractUserService jdbcUserService;

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
        Film createdFilm = jdbcFilmRepository.save(film);
        return updateFilm(createdFilm);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || getFilmById(film.getId()) == null) {
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
        User user = jdbcUserService.getUserById(userId);
        Film film = getFilmById(filmId);
        Likes like = new Likes(userId, filmId);
        if (jdbcLikesRepository.findById(like) == null) {
            jdbcLikesRepository.save(like);
            film.getLikedUsers().add(user.getId());
            log.info("User with id={} has liked film '{}'.", user.getId(), film.getName());
        } else {
            log.info("User with id={} already has liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {
        User user = jdbcUserService.getUserById(userId);
        Film film = getFilmById(filmId);
        Likes like = new Likes(userId, filmId);
        if (jdbcLikesRepository.delete(like) != null) {
            log.info("User with id={} has removed like from film '{}'.", user.getId(), film.getName());
            film.getLikedUsers().remove(user.getId());
        } else {
            log.info("User with id={} has not liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    @Override
    public Iterable<Film> getMostPopularFilms(int count) {
        Collection<Film> selectedPopular = jdbcFilmRepository
                .findFirstNTopRows(count);
        if (selectedPopular.isEmpty()) {
            return jdbcFilmRepository
                    .findAll()
                    .stream()
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return selectedPopular;
        }
    }

    @Override
    public Integer deleteFilm(Film film) {
        Film deletedFilm = jdbcFilmRepository.delete(film);
        if (deletedFilm == null) {
            throw new FilmNotFoundException(film);
        } else {
            return film.getId();
        }
    }
}
