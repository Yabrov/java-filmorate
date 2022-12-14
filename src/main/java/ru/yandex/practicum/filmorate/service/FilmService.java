package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final Comparator<Film> filmPopularityComparator = Comparator
            .comparing((Film film) -> film.getLikesCount().intValue()).reversed();

    private final AbstractRepository<Film> filmRepository;
    private final UserService userService;

    public Film getFilmById(Integer filmId) {
        Film film = filmRepository.findById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId);
        }
        return film;
    }

    public Film createFilm(Film film) {
        return filmRepository.save(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new FilmNotFoundException(film);
        }
        return filmRepository.update(film);
    }

    public Iterable<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film addLike(Integer userId, Integer filmId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        boolean isAdded = user.getLikedFilms().add(filmId);
        if (isAdded) {
            log.info("User with id={} has liked film '{}'.", user.getId(), film.getName());
            film.getLikesCount().incrementAndGet();
        } else {
            log.info("User with id={} already has liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    public Film removeLike(Integer userId, Integer filmId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        boolean isRemoved = user.getLikedFilms().remove(filmId);
        if (isRemoved) {
            log.info("User with id={} has removed like from film '{}'.", user.getId(), film.getName());
            film.getLikesCount().decrementAndGet();
        } else {
            log.info("User with id={} has not liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    public Iterable<Film> getMostPopularFilms(int count) {
        return filmRepository
                .findAll()
                .stream()
                .sorted(filmPopularityComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
