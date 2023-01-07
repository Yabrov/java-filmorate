package ru.yandex.practicum.filmorate.storage.in_memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryRatingRepository implements AbstractRepository<Integer, Rating> {

    private Integer nextId = 6;

    private final Map<Integer, String> ratingMap = new HashMap<>() {{
        put(1, "G");
        put(2, "PG");
        put(3, "PG-13");
        put(4, "R");
        put(5, "NC-17");
    }};

    @Override
    public Rating save(Rating rating) {
        int id = getNextId();
        ratingMap.put(id, rating.getName());
        return rating.withId(id);
    }

    @Override
    public Rating update(Rating rating) {
        if (ratingMap.containsKey(rating.getId())) {
            ratingMap.replace(rating.getId(), rating.getName());
            return rating;
        } else {
            return null;
        }
    }

    @Override
    public Rating delete(Rating rating) {
        if (ratingMap.containsKey(rating.getId())) {
            ratingMap.remove(rating.getId());
            return rating;
        } else {
            return null;
        }
    }

    @Override
    public Rating findById(Integer id) {
        if (ratingMap.containsKey(id)) {
            return new Rating(id, ratingMap.get(id));
        } else {
            return null;
        }
    }

    @Override
    public Collection<Rating> findAll() {
        return ratingMap
                .entrySet()
                .stream()
                .map(entry -> new Rating(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Rating> findByIds(Collection<Integer> ids) {
        return ids
                .stream()
                .filter(ratingMap::containsKey)
                .map(id -> new Rating(id, ratingMap.get(id)))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Rating> findFirstNTopRows(Integer n) {
        throw new UnsupportedOperationException();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
