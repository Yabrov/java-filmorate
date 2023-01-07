package ru.yandex.practicum.filmorate.storage.in_memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
public class InMemoryLikesRepository implements AbstractRepository<Likes, Likes> {

    private final Set<Likes> likesSet = new HashSet<>();

    @Override
    public Likes save(Likes likes) {
        if (likesSet.add(likes)) {
            return likes;
        } else {
            return null;
        }
    }

    @Override
    public Likes update(Likes likes) {
        return likes;
    }

    @Override
    public Likes delete(Likes likes) {
        if (likesSet.remove(likes)) {
            return likes;
        } else {
            return null;
        }
    }

    @Override
    public Likes findById(Likes id) {
        if (likesSet.contains(id)) {
            return id;
        } else {
            return null;
        }
    }

    @Override
    public Collection<Likes> findAll() {
        return likesSet;
    }

    @Override
    public Collection<Likes> findByIds(Collection<Likes> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Likes> findFirstNTopRows(Integer n) {
        throw new UnsupportedOperationException();
    }
}
