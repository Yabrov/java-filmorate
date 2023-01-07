package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@With
@Value
@NotNull
public class FilmGenre {

    @NotNull
    Integer filmId;

    @NotNull
    Integer genreId;
}
