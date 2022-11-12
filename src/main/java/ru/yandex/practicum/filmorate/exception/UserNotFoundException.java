package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

@Getter
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(User user) {
        super("User with id=" + user.getId() + " doesn't exist.");
    }
}
