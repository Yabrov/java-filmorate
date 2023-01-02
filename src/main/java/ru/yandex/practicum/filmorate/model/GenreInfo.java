package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Value
@NotNull
public class GenreInfo {

    @With
    @NotNull
    Integer filmId;

    @With
    @NotNull
    Integer genreId;
}
