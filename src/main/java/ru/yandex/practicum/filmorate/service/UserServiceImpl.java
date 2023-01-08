package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final AbstractRepository<Integer, User> userRepository;
    private final AbstractRepository<Friends, Friends> friendsRepository;

    @Autowired
    public UserServiceImpl(
            @Qualifier("jdbcUserRepository") AbstractRepository<Integer, User> userRepository,
            @Qualifier("jdbcFriendsRepository") AbstractRepository<Friends, Friends> friendsRepository) {
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
    }

    @Override
    public User getUserById(Integer userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        return isBlank(user.getName())
                ? userRepository.save(user.withName(user.getLogin()))
                : userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || getUserById(user.getId()) == null) {
            throw new UserNotFoundException(user);
        }
        return isBlank(user.getName())
                ? userRepository.update(user.withName(user.getLogin()))
                : userRepository.update(user);
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addFriend(Integer friendId, Integer userId) {
        if (friendId.equals(userId)) {
            throw new IllegalArgumentException("User must not be friend of himself.");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Friends friends = new Friends(userId, friendId, FriendsStatus.REQUESTED);
        if (friendsRepository.save(friends) != null) {
            user.getFriends().add(friend.getId());
            log.info("User with id {} adds friend with id {}.", userId, friendId);
        } else {
            log.info("User with id {} already has friend with id {}.", userId, friendId);
        }
        return user;
    }

    @Override
    public User removeFriend(Integer friendId, Integer userId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Friends friends = new Friends(userId, friendId, FriendsStatus.REQUESTED);
        if (friendsRepository.delete(friends) != null) {
            log.info("User with id {} removes friend with id {}.", userId, friendId);
            user.getFriends().remove(friend.getId());
        } else {
            log.info("Users with id {} has not friend with id {}", userId, friendId);
        }
        return user;
    }

    @Override
    public Iterable<User> getMutualFriends(Integer id, Integer otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        Set<Integer> mutualFriendsSet = new HashSet<>(user.getFriends());
        mutualFriendsSet.retainAll(otherUser.getFriends());
        return userRepository.findByIds(mutualFriendsSet);
    }

    @Override
    public Iterable<User> getUserFriends(Integer userId) {
        return userRepository.findByIds(getUserById(userId).getFriends());
    }

    @Override
    public Integer deleteUser(User user) {
        User deletedUser = userRepository.delete(user);
        if (deletedUser == null) {
            throw new UserNotFoundException(user);
        } else {
            return deletedUser.getId();
        }
    }
}
