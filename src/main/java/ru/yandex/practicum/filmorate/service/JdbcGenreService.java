package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@Service
@RequiredArgsConstructor
public class JdbcGenreService implements AbstractGenreService {

    private final AbstractRepository<Integer, Genre> jdbcGenreRepository;

    @Override
    public Genre getGenreById(Integer genreId) {
        Genre genre = jdbcGenreRepository.findById(genreId);
        if (genre == null) {
            throw new GenreNotFoundException(genreId);
        }
        return genre;
    }

    @Override
    public Genre createGenre(Genre genre) {
        return jdbcGenreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        if (genre.getId() == null || getGenreById(genre.getId()) == null) {
            throw new GenreNotFoundException(genre);
        }
        return jdbcGenreRepository.update(genre);
    }

    @Override
    public Iterable<Genre> getAllGenres() {
        return jdbcGenreRepository.findAll();
    }

    @Override
    public Integer deleteGenre(Integer genreId) {
        Genre genre = getGenreById(genreId);
        jdbcGenreRepository.delete(genre);
        return genre.getId();
    }
}
