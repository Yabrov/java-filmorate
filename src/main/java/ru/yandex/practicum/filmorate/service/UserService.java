package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final AbstractRepository<User> userRepository;

    public User getUserById(Integer userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    public User createUser(User user) {
        return isBlank(user.getName())
                ? userRepository.save(user.withName(user.getLogin()))
                : userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new UserNotFoundException(user);
        }
        return isBlank(user.getName())
                ? userRepository.update(user.withName(user.getLogin()))
                : userRepository.update(user);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addFriend(Integer friendId, Integer userId) {
        if (friendId.equals(userId)) {
            throw new IllegalArgumentException("User must not be friend of himself.");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        boolean isAdded = user.getFriends().add(friendId);
        if (isAdded) {
            log.info("Users with ids [{}, {}] are friends now.", userId, friendId);
            friend.getFriends().add(userId);
        } else {
            log.info("Users with ids [{}, {}] are friends already.", userId, friendId);
        }
        return user;
    }

    public User removeFriend(Integer friendId, Integer userId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        boolean isRemoved = user.getFriends().remove(friendId);
        if (isRemoved) {
            log.info("Users with ids [{}, {}] are not friends now.", userId, friendId);
            friend.getFriends().remove(userId);
        } else {
            log.info("Users with ids [{}, {}] have not been friends", userId, friendId);
        }
        return user;
    }

    public Iterable<User> getMutualFriends(Integer id, Integer otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        Set<Integer> mutualFriendsSet = new HashSet<>(user.getFriends());
        mutualFriendsSet.retainAll(otherUser.getFriends());
        return mutualFriendsSet
                .stream()
                .map(userRepository::findById)
                .collect(Collectors.toList());
    }

    public Iterable<User> getUserFriends(Integer userId) {
        return getUserById(userId)
                .getFriends()
                .stream()
                .map(userRepository::findById)
                .collect(Collectors.toList());
    }
}
