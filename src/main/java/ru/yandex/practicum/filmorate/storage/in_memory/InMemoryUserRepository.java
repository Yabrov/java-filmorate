package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryUserRepository implements AbstractRepository<Integer, User> {

    private static final Comparator<User> userPopularityComparator = Comparator
            .comparing((User user) -> user.getFriends().size()).reversed();

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AbstractRepository<Likes, Likes> likesRepository;
    private final AbstractRepository<Friends, Friends> friendsRepository;
    private Integer nextId = 1;

    @Autowired
    public InMemoryUserRepository(
            @Qualifier("inMemoryLikesRepository") AbstractRepository<Likes, Likes> likesRepository,
            @Qualifier("inMemoryFriendsRepository") AbstractRepository<Friends, Friends> friendsRepository) {
        this.likesRepository = likesRepository;
        this.friendsRepository = friendsRepository;
    }

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
        return findById(user.getId());
    }

    @Override
    public User delete(User user) {
        User deletedUser = findById(user.getId());
        if (deletedUser != null) {
            deletedUser.getFriends().forEach(id -> friendsRepository
                    .delete(new Friends(user.getId(), id, FriendsStatus.REQUESTED)));
            deletedUser.getLikedFilms().forEach(id -> likesRepository
                    .delete(new Likes(user.getId(), id)));
            users.remove(user.getId());
        }
        return deletedUser;
    }

    @Override
    public User findById(Integer userId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().addAll(getFriendsByUserId(userId, friendsRepository.findAll()));
            user.getLikedFilms().addAll(getLikedFilmsByUserId(userId, likesRepository.findAll()));
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        Collection<User> userList = users.values();
        Collection<Friends> friends = friendsRepository.findAll();
        Collection<Likes> likes = likesRepository.findAll();
        for (User user : userList) {
            user.getFriends().addAll(getFriendsByUserId(user.getId(), friends));
            user.getLikedFilms().addAll(getLikedFilmsByUserId(user.getId(), likes));
        }
        return userList;
    }

    @Override
    public Collection<User> findByIds(Collection<Integer> ids) {
        return ids
                .stream()
                .map(this::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findFirstNTopRows(Integer n) {
        return findAll()
                .stream()
                .sorted(userPopularityComparator)
                .limit(n)
                .collect(Collectors.toList());
    }

    private Collection<Integer> getLikedFilmsByUserId(Integer userId, Collection<Likes> likes) {
        return likes
                .stream()
                .filter(l -> l.getUserId().equals(userId))
                .map(Likes::getFilmId)
                .collect(Collectors.toList());
    }

    private Collection<Integer> getFriendsByUserId(Integer userId, Collection<Friends> friends) {
        return friends
                .stream()
                .filter(f -> f.getUserId().equals(userId))
                .map(Friends::getFriendId)
                .collect(Collectors.toList());
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
