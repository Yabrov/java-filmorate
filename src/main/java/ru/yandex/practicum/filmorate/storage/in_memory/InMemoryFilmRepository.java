package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryFilmRepository implements AbstractRepository<Integer, Film> {

    private static final Comparator<Film> filmPopularityComparator = Comparator
            .comparing((Film film) -> film.getLikedUsers().size()).reversed();

    private final Map<Integer, FilmInfo> films = new HashMap<>();

    private final AbstractRepository<Likes, Likes> likesRepository;
    private final AbstractRepository<Integer, Genre> genreRepository;
    private final AbstractRepository<Integer, Rating> ratingRepository;
    private Integer nextId = 1;

    @Autowired
    public InMemoryFilmRepository(
            @Qualifier("inMemoryLikesRepository") AbstractRepository<Likes, Likes> likesRepository,
            @Qualifier("inMemoryGenreRepository") AbstractRepository<Integer, Genre> genreRepository,
            @Qualifier("inMemoryRatingRepository") AbstractRepository<Integer, Rating> ratingRepository) {
        this.likesRepository = likesRepository;
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Film save(Film film) {
        Integer newId = getNextId();
        FilmInfo persistedFilm = new FilmInfo(film.withId(newId));
        log.info("{} created.", persistedFilm);
        films.put(persistedFilm.getId(), persistedFilm);
        return film.withId(newId);
    }

    @Override
    public Film update(Film film) {
        FilmInfo oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            throw new FilmNotFoundException(film);
        }
        log.info("Film with id={} has been updated.", film.getId());
        films.replace(film.getId(), oldFilm, new FilmInfo(film));
        return findById(film.getId());
    }

    @Override
    public Film delete(Film film) {
        Film deletedFilm = findById(film.getId());
        if (deletedFilm != null) {
            deletedFilm.getLikedUsers().forEach(id -> likesRepository
                    .delete(new Likes(id, film.getId())));
            films.remove(film.getId());
        }
        return deletedFilm;
    }

    @Override
    public Film findById(Integer id) {
        FilmInfo film = films.get(id);
        if (film != null) {
            return getFilmFromFilmInfo(film);
        } else {
            return null;
        }
    }

    @Override
    public Collection<Film> findAll() {
        return films
                .values()
                .stream()
                .map(this::getFilmFromFilmInfo)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findByIds(Collection<Integer> ids) {
        return ids
                .stream()
                .map(this::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findFirstNTopRows(Integer n) {
        return findAll()
                .stream()
                .sorted(filmPopularityComparator)
                .limit(n)
                .collect(Collectors.toList());
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }

    private Film getFilmFromFilmInfo(FilmInfo filmInfo) {
        Film film = Film.builder()
                .id(filmInfo.getId())
                .name(filmInfo.name)
                .description(filmInfo.description)
                .releaseDate(filmInfo.releaseDate)
                .duration(filmInfo.duration)
                .rating(ratingRepository.findById(filmInfo.ratingId))
                .genres(new TreeSet<>(genreRepository.findByIds(filmInfo.genreIds)))
                .build();
        film.getLikedUsers().addAll(getLikedUsersId(film.getId()));
        return film;
    }

    private Collection<Integer> getLikedUsersId(Integer filmId) {
        return likesRepository
                .findAll()
                .stream()
                .filter(l -> l.getFilmId().equals(filmId))
                .map(Likes::getUserId)
                .collect(Collectors.toList());
    }

    @Value
    private static class FilmInfo {
        Integer id;
        String name;
        String description;
        LocalDate releaseDate;
        Integer duration;
        Integer ratingId;
        Set<Integer> genreIds;

        public FilmInfo(Film film) {
            id = film.getId();
            name = film.getName();
            description = film.getDescription();
            releaseDate = film.getReleaseDate();
            duration = film.getDuration();
            if (film.getRating() == null) {
                ratingId = null;
            } else {
                ratingId = film.getRating().getId();
            }
            genreIds = film
                    .getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
        }
    }
}
