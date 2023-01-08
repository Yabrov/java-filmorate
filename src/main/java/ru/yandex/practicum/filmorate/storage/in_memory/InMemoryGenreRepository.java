package ru.yandex.practicum.filmorate.storage.in_memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryGenreRepository implements AbstractRepository<Integer, Genre> {

    private Integer nextId = 7;

    private final Map<Integer, String> genreMap = new HashMap<>() {{
        put(1, "Комедия");
        put(2, "Драма");
        put(3, "Мультфильм");
        put(4, "Триллер");
        put(5, "Документальный");
        put(6, "Боевик");
    }};

    @Override
    public Genre save(Genre genre) {
        int id = getNextId();
        genreMap.put(id, genre.getName());
        return genre.withId(id);
    }

    @Override
    public Genre update(Genre genre) {
        if (genreMap.containsKey(genre.getId())) {
            genreMap.replace(genre.getId(), genre.getName());
            return genre;
        } else {
            return null;
        }
    }

    @Override
    public Genre delete(Genre genre) {
        if (genreMap.containsKey(genre.getId())) {
            genreMap.remove(genre.getId());
            return genre;
        } else {
            return null;
        }
    }

    @Override
    public Genre findById(Integer id) {
        if (genreMap.containsKey(id)) {
            return new Genre(id, genreMap.get(id));
        } else {
            return null;
        }
    }

    @Override
    public Collection<Genre> findAll() {
        return genreMap
                .entrySet()
                .stream()
                .map(entry -> new Genre(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Genre> findByIds(Collection<Integer> ids) {
        return ids
                .stream()
                .filter(genreMap::containsKey)
                .map(id -> new Genre(id, genreMap.get(id)))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Genre> findFirstNTopRows(Integer n) {
        throw new UnsupportedOperationException();
    }

    private synchronized Integer getNextId() {
        return nextId++;
    }
}
