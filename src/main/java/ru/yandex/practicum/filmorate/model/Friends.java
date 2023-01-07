package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import java.util.Objects;

@Value
public class Friends {

    Integer userId;

    Integer friendId;

    @With
    FriendsStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friends)) return false;
        Friends friends = (Friends) o;
        return Objects.equals(userId, friends.userId)
                && Objects.equals(friendId, friends.friendId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
