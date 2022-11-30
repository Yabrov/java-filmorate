package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User persistUser(User user);

    User replaceUser(User user);

    User deleteUser(User user);

    User getUser(Integer userId);

    Collection<User> getAllUsers();
}
