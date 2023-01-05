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
public class InMemoryFilmService implements AbstractFilmService {

    private static final Comparator<Film> filmPopularityComparator = Comparator
            .comparing((Film film) -> film.getLikedUsers().size()).reversed();

    private final AbstractRepository<Integer, Film> inMemoryFilmRepository;
    private final InMemoryUserService inMemoryUserService;

    @Override
    public Film getFilmById(Integer filmId) {
        Film film = inMemoryFilmRepository.findById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId);
        }
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        return inMemoryFilmRepository.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new FilmNotFoundException(film);
        }
        return inMemoryFilmRepository.update(film);
    }

    @Override
    public Iterable<Film> getAllFilms() {
        return inMemoryFilmRepository.findAll();
    }

    @Override
    public Film addLike(Integer userId, Integer filmId) {
        User user = inMemoryUserService.getUserById(userId);
        Film film = getFilmById(filmId);
        boolean isAdded = user.getLikedFilms().add(filmId);
        if (isAdded) {
            log.info("User with id={} has liked film '{}'.", user.getId(), film.getName());
            film.getLikedUsers().add(user.getId());
        } else {
            log.info("User with id={} already has liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {
        User user = inMemoryUserService.getUserById(userId);
        Film film = getFilmById(filmId);
        boolean isRemoved = user.getLikedFilms().remove(filmId);
        if (isRemoved) {
            log.info("User with id={} has removed like from film '{}'.", user.getId(), film.getName());
            film.getLikedUsers().remove(user.getId());
        } else {
            log.info("User with id={} has not liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    @Override
    public Iterable<Film> getMostPopularFilms(int count) {
        return inMemoryFilmRepository
                .findAll()
                .stream()
                .sorted(filmPopularityComparator)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Integer deleteFilm(Film film) {
        Film deletedFilm = inMemoryFilmRepository.delete(film);
        if (deletedFilm == null) {
            throw new FilmNotFoundException(film);
        } else {
            return film.getId();
        }
    }
}
