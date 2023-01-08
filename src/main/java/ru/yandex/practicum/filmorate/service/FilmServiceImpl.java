package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final AbstractRepository<Integer, Film> filmRepository;
    private final AbstractRepository<Likes, Likes> likesRepository;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(
            @Qualifier("jdbcFilmRepository") AbstractRepository<Integer, Film> filmRepository,
            @Qualifier("jdbcLikesRepository") AbstractRepository<Likes, Likes> likesRepository,
            @Qualifier("userServiceImpl") UserService userService) {
        this.filmRepository = filmRepository;
        this.likesRepository = likesRepository;
        this.userService = userService;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        Film film = filmRepository.findById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId);
        }
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        Film createdFilm = filmRepository.save(film);
        return updateFilm(createdFilm);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || getFilmById(film.getId()) == null) {
            throw new FilmNotFoundException(film);
        }
        return filmRepository.update(film);
    }

    @Override
    public Iterable<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Film addLike(Integer userId, Integer filmId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        Likes like = new Likes(userId, filmId);
        if (likesRepository.save(like) != null) {
            film.getLikedUsers().add(user.getId());
            log.info("User with id={} has liked film '{}'.", user.getId(), film.getName());
        } else {
            log.info("User with id={} already has liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    @Override
    public Film removeLike(Integer userId, Integer filmId) {
        User user = userService.getUserById(userId);
        Film film = getFilmById(filmId);
        Likes like = new Likes(userId, filmId);
        if (likesRepository.delete(like) != null) {
            log.info("User with id={} has removed like from film '{}'.", user.getId(), film.getName());
            film.getLikedUsers().remove(user.getId());
        } else {
            log.info("User with id={} has not liked film '{}'.", user.getId(), film.getName());
        }
        return film;
    }

    @Override
    public Iterable<Film> getMostPopularFilms(int count) {
        return filmRepository.findFirstNTopRows(count);
    }

    @Override
    public Integer deleteFilm(Film film) {
        Film deletedFilm = filmRepository.delete(film);
        if (deletedFilm == null) {
            throw new FilmNotFoundException(film);
        } else {
            return film.getId();
        }
    }
}
