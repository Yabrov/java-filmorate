package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryUserRepository implements AbstractRepository<User> {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    private Integer nextId = 1;

    @Override
    public User save(User user) {
        User persistedUser = user.withId(getNextId());
        users.put(persistedUser.getId(), persistedUser);
        log.info("{} created.", persistedUser);
        return persistedUser;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(user);
        }
        log.info("User with id={} has been updated.", user.getId());
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User delete(User user) {
        return users.remove(user.getId());
    }

    @Override
    public User findById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
