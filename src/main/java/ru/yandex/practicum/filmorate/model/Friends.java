package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

@Value
public class Friends {

    Integer userId;

    Integer friendId;

    @With
    FriendsStatus status;
}
