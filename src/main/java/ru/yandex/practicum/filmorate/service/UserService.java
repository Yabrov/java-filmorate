package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();

    private Integer nextId = 1;

    public User createUser(User user) {
        User newUser;
        if (isBlank(user.getName())) {
            newUser = user.withId(nextId++).withName(user.getLogin());
        } else {
            newUser = user.withId(nextId++);
        }
        log.info("{} created.", newUser);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public User updateUser(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new UserNotFoundException(user);
        }
        if (isBlank(user.getName())) {
            user = user.withName(user.getLogin());
        }
        log.info("Пользователь с id={} успешно обновлен.", user.getId());
        users.replace(user.getId(), user);
        return user;
    }

    public Iterable<User> getAllUsers() {
        return users.values();
    }
}
