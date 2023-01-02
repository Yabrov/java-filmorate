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

@RequiredArgsConstructor
@Repository("jdbcFilmRepository")
public class JdbcFilmRepository implements AbstractRepository<Integer, Film> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmMapper;
    private final RowMapper<Genre> genreMapper;

    @Override
    public Film save(Film film) {
        String queryString = "INSERT INTO public.film(name, description, " +
                "release_date, duration, rating_id) VALUES(?, ?, CAST(? AS DATE), ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(queryString, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setString(3, LocalDateConvertor.toSqlString(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getRating().getId());
                return stmt;
            }, keyHolder);
            return film.withId(keyHolder.getKey().intValue());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for new film.";
            throw new JdbcQueryExecutionException(mes, e);
        } catch (NullPointerException e) {
            String mes = "Returned id in null. New film has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Film update(Film film) {
        String queryString = "UPDATE public.film SET name = ?, description = ?, " +
                "release_date = CAST(? AS DATE), duration = ?, rating_id = ?";
        try {
            jdbcTemplate.update(
                    queryString,
                    film.getName(),
                    film.getDescription(),
                    LocalDateConvertor.toSqlString(film.getReleaseDate()),
                    film.getDuration(),
                    film.getRating().getId()
            );
            return film;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for film with id=" + film.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Film delete(Film film) {
        String filmQuery = "DELETE FROM public.film WHERE id = ?";
        String likesQuery = "DELETE FROM public.likes_info WHERE film_id = ?";
        String genresQuery = "DELETE FROM public.film_genres_info WHERE film_id = ?";
        try {
            boolean isDeleted = jdbcTemplate.update(filmQuery, film.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(likesQuery, film.getId());
                jdbcTemplate.update(genresQuery, film.getId());
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
        String filmQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "f.duration, r.id AS rating_id, r.name AS rating_name FROM public.film f " +
                "LEFT JOIN public.rating r ON r.id = f.rating_id WHERE f.id = ?";
        String likesQuery = "SELECT l.user_id FROM public.likes_info l WHERE l.film_id = ?";
        String genreQuery = "SELECT g.id, g.name " +
                "FROM public.film_genres_info i " +
                "LEFT JOIN public.genre g ON g.id = i.genre_id " +
                "WHERE i.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(filmQuery, filmMapper, id);
            if (film != null) {
                film.getLikedUsers().addAll(jdbcTemplate.queryForList(likesQuery, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate.query(genreQuery, genreMapper, film.getId()));
            }
            return film;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for film with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Film> findAll() {
        String filmsQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "f.duration, f.rating_id FROM public.film f";
        String likesQuery = "SELECT l.user_id FROM public.likes_info l WHERE l.film_id = ?";
        String genreQuery = "SELECT g.id, g.name " +
                "FROM public.film_genres_info i " +
                "LEFT JOIN public.genre g ON g.id = i.genre_id " +
                "WHERE i.film_id = ?";
        try {
            List<Film> films = jdbcTemplate.query(filmsQuery, filmMapper);
            for (Film film : films) {
                film.getLikedUsers().addAll(jdbcTemplate.queryForList(likesQuery, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate.query(genreQuery, genreMapper, film.getId()));
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
                "SELECT f.id, f.name, f.description, f.release_date, " +
                        "f.duration, f.rating_id FROM public.film f WHERE f.id IN (%s)", inSql);
        String likesQuery = "SELECT l.user_id FROM public.likes_info l WHERE l.film_id = ?";
        String genreQuery = "SELECT g.id, g.name " +
                "FROM public.film_genres_info i " +
                "LEFT JOIN public.genre g ON g.id = i.genre_id " +
                "WHERE i.film_id = ?";
        try {
            List<Film> films = jdbcTemplate.query(filmsQuery, ids.toArray(), filmMapper);
            for (Film film : films) {
                film.getLikedUsers().addAll(jdbcTemplate.queryForList(likesQuery, Integer.class, film.getId()));
                film.getGenres().addAll(jdbcTemplate.query(genreQuery, genreMapper, film.getId()));
            }
            return films;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many films.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
