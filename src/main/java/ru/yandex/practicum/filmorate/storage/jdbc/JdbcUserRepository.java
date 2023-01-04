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

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements AbstractRepository<Integer, User> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;

    private static final String insertUserSqlString =
            "INSERT INTO USERS(email, login, name, birthday) VALUES(?, ?, ?, CAST(? AS DATE))";

    private static final String updateUserSqlString =
            "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = CAST(? AS DATE) WHERE id = ?";

    private static final String deleteUserSqlString = "DELETE FROM USERS WHERE id = ?";

    private static final String deleteFilmsSqlString = "DELETE FROM LIKES WHERE user_id = ?";

    private static final String deleteFriendsSqlString = "DELETE FROM FRIENDS WHERE ? IN (user_id, friend_id)";

    private static final String findUserByIdSqlString =
            "SELECT id, email, login, name, birthday FROM USERS WHERE id = ?";

    private static final String findAllUserSqlString = "SELECT id, email, login, name, birthday FROM USERS";

    private static final String findFriendsByUserIdSqlString = "SELECT friend_id FROM FRIENDS WHERE user_id = ?";

    private static final String findFilmsByUserIdSqlString = "SELECT film_id FROM LIKES WHERE user_id = ?";

    private static final String findTopPopularUsersSqlString = "" +
            "SELECT u.id, u.email, u.login, u.name, u.birthday FROM (" +
            "SELECT user_id, COUNT(*) AS cnt FROM FRIENDS " +
            "GROUP BY user_id ORDER by cnt DESC LIMIT ?) f " +
            "LEFT JOIN USERS u ON u.id = f.user_id";

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(insertUserSqlString, new String[]{"id"});
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
            String mes = "Returned id is null. New user has not been saved.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public User update(User user) {
        try {
            jdbcTemplate.update(
                    updateUserSqlString,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    LocalDateConvertor.toSqlString(user.getBirthday()),
                    user.getId()
            );
            return findById(user.getId());
        } catch (DataAccessException e) {
            String mes = "Error when execute sql save for user with id=" + user.getId() + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public User delete(User user) {
        try {
            boolean isDeleted = jdbcTemplate.update(deleteUserSqlString, user.getId()) > 0;
            if (isDeleted) {
                jdbcTemplate.update(deleteFriendsSqlString, user.getId());
                jdbcTemplate.update(deleteFilmsSqlString, user.getId());
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
        try {
            User user = jdbcTemplate.queryForObject(findUserByIdSqlString, userMapper, id);
            if (user != null) {
                user.getFriends().addAll(jdbcTemplate
                        .queryForList(findFriendsByUserIdSqlString, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate
                        .queryForList(findFilmsByUserIdSqlString, Integer.class, user.getId()));
            }
            return user;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for user with id=" + id + '.';
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<User> findAll() {
        try {
            List<User> users = jdbcTemplate.query(findAllUserSqlString, userMapper);
            for (User user : users) {
                user.getFriends().addAll(jdbcTemplate
                        .queryForList(findFriendsByUserIdSqlString, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate
                        .queryForList(findFilmsByUserIdSqlString, Integer.class, user.getId()));
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
                "SELECT id, email, login, name, birthday FROM USERS WHERE id IN (%s)", inSql);
        try {
            List<User> users = jdbcTemplate.query(userQuery, ids.toArray(), userMapper);
            for (User user : users) {
                user.getFriends().addAll(jdbcTemplate
                        .queryForList(findFriendsByUserIdSqlString, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate
                        .queryForList(findFilmsByUserIdSqlString, Integer.class, user.getId()));
            }
            return users;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for many users.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<User> findFirstNTopRows(Integer n) {
        try {
            List<User> users = jdbcTemplate.query(findTopPopularUsersSqlString, userMapper, n);
            for (User user : users) {
                user.getFriends().addAll(jdbcTemplate
                        .queryForList(findFriendsByUserIdSqlString, Integer.class, user.getId()));
                user.getLikedFilms().addAll(jdbcTemplate
                        .queryForList(findFilmsByUserIdSqlString, Integer.class, user.getId()));
            }
            return users;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for popular users.";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }
}
