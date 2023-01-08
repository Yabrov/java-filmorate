package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

@Getter
public class UserNotFoundException extends RuntimeException {

    private static final String MES_FORMAT = "User with id=%s does not exist.";

    private final User user;

    public UserNotFoundException(User user) {
        super(String.format(MES_FORMAT, user.getId()));
        this.user = user;
    }

    public UserNotFoundException(Integer userId) {
        super(String.format(MES_FORMAT, userId));
        this.user = null;
    }
}
