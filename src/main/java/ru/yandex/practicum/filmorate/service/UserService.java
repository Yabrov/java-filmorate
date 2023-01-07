package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

public interface UserService {

    User getUserById(Integer userId);

    User createUser(User user);

    User updateUser(User user);

    Iterable<User> getAllUsers();

    User addFriend(Integer friendId, Integer userId);

    User removeFriend(Integer friendId, Integer userId);

    Iterable<User> getMutualFriends(Integer id, Integer otherId);

    Iterable<User> getUserFriends(Integer userId);

    Integer deleteUser(User user);
}
