package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class JdbcLikesRepository implements AbstractRepository<Likes, Likes> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Likes> likesMapper;

    private static final String insertLikeSqlString = "MERGE INTO LIKES(user_id, film_id) VALUES(?, ?)";

    private static final String deleteLikeSqlString = "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?";

    private static final String findLikeSqlString =
            "SELECT user_id, film_id FROM LIKES WHERE user_id = ? AND film_id = ?";

    @Override
    public Likes save(Likes likes) {
        int userId = likes.getUserId();
        int filmId = likes.getFilmId();
        try {
            if (jdbcTemplate.update(insertLikeSqlString, userId, filmId) > 0) {
                return likes;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql insert like to film.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Likes update(Likes likes) {
        return likes;
    }

    @Override
    public Likes delete(Likes likes) {
        int userId = likes.getUserId();
        int filmId = likes.getFilmId();
        try {
            if (jdbcTemplate.update(deleteLikeSqlString, userId, filmId) > 0) {
                return likes;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for film like.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Likes findById(Likes like) {
        int userId = like.getUserId();
        int filmId = like.getFilmId();
        try {
            return jdbcTemplate.queryForObject(findLikeSqlString, likesMapper, userId, filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for like.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Likes> findAll() {
        throw new UnsupportedOperationException();
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
