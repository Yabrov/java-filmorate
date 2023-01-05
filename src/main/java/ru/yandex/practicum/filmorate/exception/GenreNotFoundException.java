package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Genre;

@Getter
public class GenreNotFoundException extends RuntimeException {

    private static final String MES_FORMAT = "Genre with id=%s does not exist.";

    private final Genre genre;

    public GenreNotFoundException(Genre genre) {
        super(String.format(MES_FORMAT, genre.getId()));
        this.genre = genre;
    }

    public GenreNotFoundException(Integer genreId) {
        super(String.format(MES_FORMAT, genreId));
        this.genre = null;
    }
}
