package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@Service
public class GenreServiceImpl implements GenreService {

    private final AbstractRepository<Integer, Genre> genreRepository;

    public GenreServiceImpl(
            @Qualifier("jdbcGenreRepository") AbstractRepository<Integer, Genre> genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        Genre genre = genreRepository.findById(genreId);
        if (genre == null) {
            throw new GenreNotFoundException(genreId);
        }
        return genre;
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        if (genre.getId() == null || getGenreById(genre.getId()) == null) {
            throw new GenreNotFoundException(genre);
        }
        return genreRepository.update(genre);
    }

    @Override
    public Iterable<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Integer deleteGenre(Integer genreId) {
        Genre genre = getGenreById(genreId);
        genreRepository.delete(genre);
        return genre.getId();
    }
}
