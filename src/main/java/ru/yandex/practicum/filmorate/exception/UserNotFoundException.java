package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final User user;

    public UserNotFoundException(User user) {
        super("User with id=" + user.getId() + " doesn't exist.");
        this.user = user;
    }

    public UserNotFoundException(Integer userId) {
        super("User with id=" + userId + " doesn't exist.");
        this.user = null;
    }
}
