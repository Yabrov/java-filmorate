package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;
import ru.yandex.practicum.filmorate.utils.LocalDateConvertor;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Repository("jdbcUserRepository")
public class JdbcUserRepository implements AbstractRepository<Integer, User> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;

    @Override
    public User save(User user) {
        String queryString = "INSERT INTO public.user(email, login, name, birthday) " +
                "VALUES(?, ?, ?, CAST(? AS DATE))";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(queryString, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setString(4, LocalDateConvertor.toSqlString(user.getBirthday()));
                return stmt;
            }, keyHolder);
            return user.withId(keyHolder.getKey().intValue());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for new user.";
            throw new JdbcQueryExecutionException(mes, e);
        } catch (NullPointerException e) {
            String mes = "Returned id in null. New user has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public User update(User user) {
        String queryString = "UPDATE public.user SET email = ?, login = ?, " +
                "name = ?, birthday = CAST(? AS DATE)) WHERE id = ?";
        try {
            jdbcTemplate.update(
                    queryString,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    LocalDateConvertor.toSqlString(user.getBirthday()),
                    user.getId()
            );
            return user;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for user with id=" + user.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public User delete(User user) {
        String userQuery = "DELETE FROM public.user WHERE id = ?";
        String friendsQuery = "DELETE FROM public.friendship_info WHERE ? IN (user_id, friend_id)";
        String filmQuery = "DELETE FROM public.likes_info WHERE user_id = ?";
        try {
            boolean isDeleted = jdbcTemplate.update(userQuery, user.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(friendsQuery, user.getId());
                jdbcTemplate.update(filmQuery, user.getId());
                return user;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for user with id=" + user.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public User findById(Integer id) {
        String userQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM public.user u WHERE u.id = ?";
        String friendsQuery = "SELECT f.friend_id FROM public.friendship_info f WHERE f.user_id = ?";
        String filmsQuery = "SELECT l.film_id FROM public.likes_info l WHERE l.user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(userQuery, userMapper, id);
            if (user != null) {
                user.getFriends().addAll(jdbcTemplate.queryForList(friendsQuery, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate.queryForList(filmsQuery, Integer.class, user.getId()));
            }
            return user;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for user with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<User> findAll() {
        String userQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM public.user u";
        String friendsQuery = "SELECT f.friend_id FROM public.friendship_info f WHERE f.user_id = ?";
        String filmsQuery = "SELECT l.film_id FROM public.likes_info l WHERE l.user_id = ?";
        try {
            List<User> users = jdbcTemplate.query(userQuery, userMapper);
            for (User user : users) {
                user.getFriends().addAll(jdbcTemplate.queryForList(friendsQuery, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate.queryForList(filmsQuery, Integer.class, user.getId()));
            }
            return users;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for all users.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<User> findByIds(Collection<Integer> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String userQuery = String.format(
                "SELECT u.id, u.email, u.login, u.name, u.birthday FROM public.user u WHERE u.id IN (%s)", inSql);
        String friendsQuery = "SELECT f.friend_id FROM public.friendship_info f WHERE f.user_id = ?";
        String filmsQuery = "SELECT l.film_id FROM public.likes_info l WHERE l.user_id = ?";
        try {
            List<User> users = jdbcTemplate.query(userQuery, ids.toArray(), userMapper);
            for (User user : users) {
                user.getFriends().addAll(jdbcTemplate.queryForList(friendsQuery, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate.queryForList(filmsQuery, Integer.class, user.getId()));
            }
            return users;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many users.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
