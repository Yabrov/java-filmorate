package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Value
@Builder
@NotNull
public class Likes {

    @With
    @NotNull
    Integer userId;

    @With
    @NotNull
    Integer filmId;

    public Likes(Integer userId, Integer filmId) {
        this.userId = userId;
        this.filmId = filmId;
    }
}
