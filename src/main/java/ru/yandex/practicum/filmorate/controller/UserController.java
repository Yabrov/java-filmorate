package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.AbstractUserService;

import javax.validation.Valid;

@RestController
public class UserController {

    private final AbstractUserService userService;

    @Autowired
    public UserController(@Qualifier("jdbcUserService") AbstractUserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User addFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        return userService.addFriend(friendId, userId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User removeFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        return userService.removeFriend(friendId, userId);
    }

    @GetMapping(value = "/users/{id}/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<User> getUserFriends(@PathVariable("id") Integer userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<User> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}
