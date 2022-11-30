package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    private Integer nextId = 1;

    @Override
    public User persistUser(User user) {
        User persistedUser = user.withId(getNextId());
        users.put(persistedUser.getId(), persistedUser);
        log.info("{} created.", persistedUser);
        return persistedUser;
    }

    @Override
    public User replaceUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(user);
        }
        log.info("Пользователь с id={} успешно обновлен.", user.getId());
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User deleteUser(User user) {
        return users.remove(user.getId());
    }

    @Override
    public User getUser(Integer userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
