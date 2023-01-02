package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Repository("jdbcRatingRepository")
public class JdbcRatingRepository implements AbstractRepository<Integer, Rating> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Rating save(Rating rating) {
        return null;
    }

    @Override
    public Rating update(Rating rating) {
        return null;
    }

    @Override
    public Rating delete(Rating rating) {
        return null;
    }

    @Override
    public Rating findById(Integer id) {
        return null;
    }

    @Override
    public Collection<Rating> findAll() {
        return null;
    }

    @Override
    public Collection<Rating> findByIds(Collection<Integer> ids) {
        return null;
    }
}
