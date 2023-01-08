package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@With
@Value
@NotNull
public class Likes {

    @NotNull
    Integer userId;

    @NotNull
    Integer filmId;
}
