package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Value
@Builder
@NotNull
public class FriendshipInfo {

    @With
    @NotNull
    Integer userId;

    @With
    @NotNull
    Integer friendId;
}
