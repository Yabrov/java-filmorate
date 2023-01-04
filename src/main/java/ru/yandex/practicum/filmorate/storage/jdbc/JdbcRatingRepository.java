package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class JdbcRatingRepository implements AbstractRepository<Integer, Rating> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Rating> ratingMapper;

    private static final String insertRatingSqlString = "INSERT INTO RATINGS(name) VALUES(?)";

    private static final String updateRatingSqlString = "UPDATE RATINGS SET name = ? WHERE id = ?";

    private static final String deleteRatingSqlString = "DELETE FROM RATINGS WHERE id = ?";

    private static final String updateFilmSqlString = "UPDATE FILMS SET rating_id = NULL WHERE rating_id = ?";

    private static final String findRatingByIdSqlString = "SELECT id, name FROM RATINGS WHERE id = ?";

    private static final String findAllRatingsSqlString = "SELECT id, name FROM RATINGS WHERE id = ?";

    private static final String findTopPopularRatingsSqlString = "" +
            "SELECT r.id, r.name FROM (" +
            "SELECT rating_id, COUNT(*) AS cnt FROM FILMS " +
            "GROUP BY rating_id ORDER by cnt DESC LIMIT ?) p " +
            "LEFT JOIN RATINGS r ON r.id = p.rating_id";

    @Override
    public Rating save(Rating rating) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection
                        .prepareStatement(insertRatingSqlString, new String[]{"id"});
                stmt.setString(1, rating.getName());
                return stmt;
            }, keyHolder);
            return new Rating(keyHolder.getKey().intValue(), rating.getName());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for new rating.";
            throw new JdbcQueryExecutionException(mes, e);
        } catch (NullPointerException e) {
            String mes = "Returned id is null. New rating has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Rating update(Rating rating) {
        try {
            jdbcTemplate.update(updateRatingSqlString, rating.getName(), rating.getId());
            return rating;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for rating with id=" + rating.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Rating delete(Rating rating) {
        try {
            boolean isDeleted = jdbcTemplate.update(deleteRatingSqlString, rating.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(updateFilmSqlString, rating.getId());
                return rating;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for rating with id=" + rating.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Rating findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(findRatingByIdSqlString, ratingMapper, id);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for rating with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Rating> findAll() {
        try {
            return jdbcTemplate.query(findAllRatingsSqlString, ratingMapper);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for all ratings.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Rating> findByIds(Collection<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String userQuery = String.format("SELECT id, name FROM RATINGS WHERE id IN (%s)", inSql);
        try {
            return jdbcTemplate.query(userQuery, ids.toArray(), ratingMapper);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many ratings.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Rating> findFirstNTopRows(Integer n) {
        try {
            return jdbcTemplate.query(findTopPopularRatingsSqlString, ratingMapper, n);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for popular ratings.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
