package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
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

@RequiredArgsConstructor
@Repository("jdbcGenreRepository")
public class JdbcGenreRepository implements AbstractRepository<Integer, Genre> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreMapper;

    @Override
    public Genre save(Genre genre) {
        String queryString = "INSERT INTO public.genre(name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(queryString, new String[]{"id"});
                stmt.setString(1, genre.getName());
                return stmt;
            }, keyHolder);
            return new Genre(keyHolder.getKey().intValue(), genre.getName());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for new genre.";
            throw new JdbcQueryExecutionException(mes, e);
        } catch (NullPointerException e) {
            String mes = "Returned id in null. New genre has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Genre update(Genre genre) {
        String queryString = "UPDATE public.genre SET name = ? WHERE id = ?";
        try {
            jdbcTemplate.update(queryString, genre.getName(), genre.getId());
            return genre;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for genre with id=" + genre.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Genre delete(Genre genre) {
        String genreQuery = "DELETE FROM public.genre WHERE id = ?";
        String genreInfoQuery = "DELETE FROM public.film_genres_info WHERE genre_id = ?";
        try {
            boolean isDeleted = jdbcTemplate.update(genreQuery, genre.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(genreInfoQuery, genre.getId());
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
        String genreQuery = "SELECT g.id, g.name FROM public.genre g WHERE g.id = ?";
        try {
            return jdbcTemplate.queryForObject(genreQuery, genreMapper, id);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for genre with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Genre> findAll() {
        String genreQuery = "SELECT g.id, g.name FROM public.genre g";
        try {
            return jdbcTemplate.query(genreQuery, genreMapper);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for all genres.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Genre> findByIds(Collection<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String userQuery = String.format("SELECT g.id, g.name FROM public.genre g WHERE g.id IN (%s)", inSql);
        try {
            return jdbcTemplate.query(userQuery, ids.toArray(), genreMapper);
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many genres.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
