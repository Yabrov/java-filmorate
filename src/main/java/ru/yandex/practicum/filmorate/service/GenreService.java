package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreService {

    Genre getGenreById(Integer genreId);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Iterable<Genre> getAllGenres();

    Integer deleteGenre(Integer genreId);
}
