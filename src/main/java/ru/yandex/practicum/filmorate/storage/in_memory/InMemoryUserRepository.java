package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryUserRepository implements AbstractRepository<Integer, User> {

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
        User oldUser = users.get(user.getId());
        if (oldUser == null) {
            throw new UserNotFoundException(user);
        }
        user.getLikedFilms().addAll(oldUser.getLikedFilms());
        user.getFriends().addAll(oldUser.getFriends());
        log.info("User with id={} has been updated.", user.getId());
        users.replace(user.getId(), oldUser, user);
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

    @Override
    public Collection<User> findByIds(Collection<Integer> ids) {
        return ids.stream().map(this::findById).collect(Collectors.toList());
    }

    @Override
    public Collection<User> findFirstNTopRows(Integer n) {
        return Collections.emptyList();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
