package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    public User createUser(User user) {
        return isBlank(user.getName())
                ? userStorage.persistUser(user.withName(user.getLogin()))
                : userStorage.persistUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new UserNotFoundException(user);
        }
        return isBlank(user.getName())
                ? userStorage.replaceUser(user.withName(user.getLogin()))
                : userStorage.replaceUser(user);
    }

    public Iterable<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(Integer friendId, Integer userId) {
        if (friendId.equals(userId)) {
            throw new IllegalArgumentException("User must not be friend of himself.");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        log.info("Пользователи с id=[{}, {}] теперь друзья.", userId, friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return user;
    }

    public User removeFriend(Integer friendId, Integer userId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        log.info("Пользователи с id=[{}, {}] больше не друзья.", userId, friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public Iterable<User> getMutualFriends(Integer id, Integer otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        Set<Integer> mutualFriendsSet = new HashSet<>(user.getFriends());
        mutualFriendsSet.retainAll(otherUser.getFriends());
        return mutualFriendsSet
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public Iterable<User> getUserFriends(Integer userId) {
        return getUserById(userId)
                .getFriends()
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
