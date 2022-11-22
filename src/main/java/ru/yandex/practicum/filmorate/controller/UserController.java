package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestController
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    private Integer nextId = 1;

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@Valid @RequestBody User user) {
        user.setId(nextId++);
        if (isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("{} created.", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new UserNotFoundException(user);
        }
        if (isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("Пользователь с id={} успешно обновлен.", user.getId());
        users.replace(user.getId(), user);
        return user;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<User> getAllUsers() {
        return users.values();
    }
}
