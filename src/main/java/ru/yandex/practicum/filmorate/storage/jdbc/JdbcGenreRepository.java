package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements AbstractRepository<Integer, Genre> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreMapper;

    private static final String insertGenreSqlString = "INSERT INTO GENRES(name) VALUES(?)";

    private static final String updateGenreSqlString = "UPDATE GENRES SET name = ? WHERE id = ?";

    private static final String deleteGenreSqlString = "DELETE FROM GENRES WHERE id = ?";

    private static final String deleteFilmGenreSqlString = "DELETE FROM FILM_GENRE WHERE genre_id = ?";

    private static final String findGenreByIdSqlString = "SELECT id, name FROM GENRES WHERE id = ?";

    private static final String findAllGenresSqlString = "SELECT id, name FROM GENRES";

    private static final String findTopPopularGenresSqlString = "" +
            "SELECT g.id, g.name FROM (" +
            "SELECT genre_id, COUNT(*) AS cnt FROM FILM_GENRE " +
            "GROUP BY genre_id ORDER by cnt DESC LIMIT ?) r " +
            "LEFT JOIN GENRES g ON g.id = r.genre_id";

    @Override
    public Genre save(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection
                        .prepareStatement(insertGenreSqlString, new String[]{"id"});
                stmt.setString(1, genre.getName());
                return stmt;
            }, keyHolder);
            return new Genre(keyHolder.getKey().intValue(), genre.getName());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for new genre.";
            throw new JdbcQueryExecutionException(mes, e);
        } catch (NullPointerException e) {
            String mes = "Returned id is null. New genre has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Genre update(Genre genre) {
        try {
            jdbcTemplate.update(updateGenreSqlString, genre.getName(), genre.getId());
            return genre;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for genre with id=" + genre.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Genre delete(Genre genre) {
        try {
            boolean isDeleted = jdbcTemplate.update(deleteGenreSqlString, genre.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(deleteFilmGenreSqlString, genre.getId());
                return genre;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for genre with id=" + genre.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Genre findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(findGenreByIdSqlString, genreMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for genre with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Genre> findAll() {
        try {
            return jdbcTemplate.query(findAllGenresSqlString, genreMapper);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for all genres.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Genre> findByIds(Collection<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String userQuery = String.format("SELECT id, name FROM GENRES WHERE id IN (%s)", inSql);
        try {
            return jdbcTemplate.query(userQuery, ids.toArray(), genreMapper);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many genres.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Genre> findFirstNTopRows(Integer n) {
        try {
            return jdbcTemplate.query(findTopPopularGenresSqlString, genreMapper, n);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for popular genres.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
