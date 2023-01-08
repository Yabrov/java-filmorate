package ru.yandex.practicum.filmorate.storage.in_memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryFriendsRepository implements AbstractRepository<Friends, Friends> {

    private final Map<Friends, FriendsStatus> friendsMap = new HashMap<>();

    @Override
    public Friends save(Friends friends) {
        if (friendsMap.containsKey(friends)) {
            return null;
        } else {
            friendsMap.put(friends, friends.getStatus());
            return friends;
        }
    }

    @Override
    public Friends update(Friends friends) {
        if (friendsMap.containsKey(friends)) {
            friendsMap.replace(friends, friends.getStatus());
            return friends;
        } else {
            return null;
        }
    }

    @Override
    public Friends delete(Friends friends) {
        if (friendsMap.containsKey(friends)) {
            friendsMap.remove(friends);
            return friends;
        } else {
            return null;
        }
    }

    @Override
    public Friends findById(Friends id) {
        FriendsStatus status = friendsMap.get(id);
        if (status == null) {
            return null;
        } else {
            return id.withStatus(status);
        }
    }

    @Override
    public Collection<Friends> findAll() {
        return friendsMap.keySet();
    }

    @Override
    public Collection<Friends> findByIds(Collection<Friends> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Friends> findFirstNTopRows(Integer n) {
        throw new UnsupportedOperationException();
    }
}
