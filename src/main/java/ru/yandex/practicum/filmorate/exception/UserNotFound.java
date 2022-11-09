package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

@Getter
public class UserNotFound extends RuntimeException {

    private final User user;

    public UserNotFound(User user) {
        super("User with id=" + user.getId() + " doesn't exist.");
        this.user = user;
    }
}
