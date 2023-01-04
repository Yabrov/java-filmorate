package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;
import ru.yandex.practicum.filmorate.utils.LocalDateConvertor;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements AbstractRepository<Integer, Film> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmMapper;
    private final RowMapper<Genre> genreMapper;

    private static final String insertFilmSqlString = "" +
            "INSERT INTO FILMS(name, description, release_date, duration, rating_id) " +
            "VALUES(?, ?, CAST(? AS DATE), ?, ?)";

    private static final String updateFilmSqlString = "" +
            "UPDATE FILMS SET name = ?, description = ?, " +
            "release_date = CAST(? AS DATE), duration = ?, rating_id = ?";

    private static final String deleteFilmSqlString = "DELETE FROM FILMS WHERE id = ?";

    private static final String deleteLikesSqlString = "DELETE FROM LIKES WHERE film_id = ?";

    private static final String deleteFilmGenreSqlString = "DELETE FROM FILM_GENRE WHERE film_id = ?";

    private static final String insertFilmGenreSqlString = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?)";

    private static final String findFilmByIdSqlString = "" +
            "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, r.id AS rating_id, r.name AS rating_name " +
            "FROM FILMS f LEFT JOIN RATINGS r ON r.id = f.rating_id " +
            "WHERE f.id = ?";

    private static final String findAllFilmsSqlString = "" +
            "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, r.id AS rating_id, r.name AS rating_name " +
            "FROM FILMS f LEFT JOIN RATINGS r ON r.id = f.rating_id";

    private static final String findLikesByFilmIdSqlString = "SELECT l.user_id FROM LIKES l WHERE l.film_id = ?";

    private static final String findGenresByFilmIdSqlString = "" +
            "SELECT g.id, g.name FROM FILM_GENRE i " +
            "LEFT JOIN GENRES g ON g.id = i.genre_id WHERE i.film_id = ?";

    private static final String findTopPopularFilmsSqlString = "" +
            "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, r.id AS rating_id, r.name AS rating_name FROM (" +
            "SELECT film_id, COUNT(*) AS cnt FROM LIKES " +
            "GROUP BY film_id ORDER by cnt DESC LIMIT ?) p " +
            "LEFT JOIN FILMS f ON f.id = p.film_id " +
            "LEFT JOIN RATINGS r ON r.id = f.rating_id";

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection
                        .prepareStatement(insertFilmSqlString, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setString(3, LocalDateConvertor.toSqlString(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getRating() == null ? null : film.getRating().getId());
                return stmt;
            }, keyHolder);
            return film.withId(keyHolder.getKey().intValue());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for new film.";
            throw new JdbcQueryExecutionException(mes, e);
        } catch (NullPointerException e) {
            String mes = "Returned id is null. New film has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Film update(Film film) {
        try {
            jdbcTemplate.update(
                    updateFilmSqlString,
                    film.getName(),
                    film.getDescription(),
                    LocalDateConvertor.toSqlString(film.getReleaseDate()),
                    film.getDuration(),
                    film.getRating() == null ? null : film.getRating().getId()
            );
            if (!film.getGenres().isEmpty()) {
                jdbcTemplate.update(deleteFilmGenreSqlString, film.getId());
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(insertFilmGenreSqlString, film.getId(), genre.getId());
                }
            }
            return findById(film.getId());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for film with id=" + film.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Film delete(Film film) {
        try {
            boolean isDeleted = jdbcTemplate.update(deleteFilmSqlString, film.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(deleteLikesSqlString, film.getId());
                jdbcTemplate.update(deleteFilmGenreSqlString, film.getId());
                return film;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for film with id=" + film.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Film findById(Integer id) {
        try {
            Film film = jdbcTemplate.queryForObject(findFilmByIdSqlString, filmMapper, id);
            if (film != null) {
                film.getLikedUsers().addAll(jdbcTemplate
                        .queryForList(findLikesByFilmIdSqlString, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate
                        .query(findGenresByFilmIdSqlString, genreMapper, film.getId()));
            }
            return film;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for film with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Film> findAll() {
        try {
            List<Film> films = jdbcTemplate.query(findAllFilmsSqlString, filmMapper);
            for (Film film : films) {
                film.getLikedUsers().addAll(jdbcTemplate
                        .queryForList(findLikesByFilmIdSqlString, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate
                        .query(findGenresByFilmIdSqlString, genreMapper, film.getId()));
            }
            return films;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for all films.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Film> findByIds(Collection<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String filmsQuery = String.format(
                "SELECT id, name, description, release_date, duration, rating_id FROM FILMS WHERE id IN (%s)", inSql);
        try {
            List<Film> films = jdbcTemplate.query(filmsQuery, ids.toArray(), filmMapper);
            for (Film film : films) {
                film.getLikedUsers().addAll(jdbcTemplate
                        .queryForList(findLikesByFilmIdSqlString, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate
                        .query(findGenresByFilmIdSqlString, genreMapper, film.getId()));
            }
            return films;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many films.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Film> findFirstNTopRows(Integer n) {
        try {
            List<Film> films = jdbcTemplate.query(findTopPopularFilmsSqlString, filmMapper, n);
            for (Film film : films) {
                film.getLikedUsers().addAll(jdbcTemplate
                        .queryForList(findLikesByFilmIdSqlString, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate
                        .query(findGenresByFilmIdSqlString, genreMapper, film.getId()));
            }
            return films;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for popular films.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
