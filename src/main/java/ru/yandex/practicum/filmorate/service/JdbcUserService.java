package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

public class JdbcUserService implements AbstractUserService {

    @Override
    public User getUserById(Integer userId) {
        return null;
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public Iterable<User> getAllUsers() {
        return null;
    }

    @Override
    public User addFriend(Integer friendId, Integer userId) {
        return null;
    }

    @Override
    public User removeFriend(Integer friendId, Integer userId) {
        return null;
    }

    @Override
    public Iterable<User> getMutualFriends(Integer id, Integer otherId) {
        return null;
    }

    @Override
    public Iterable<User> getUserFriends(Integer userId) {
        return null;
    }
}
