package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class JdbcFriendsRepository implements AbstractRepository<Friends, Friends> {

    private final JdbcTemplate jdbcTemplate;

    private static final String saveFriendsSqlString =
            "MERGE INTO FRIENDS(user_id, status, friend_id) VALUES(?, ?, ?)";

    private static final String updateFriendsSqlString =
            "UPDATE FRIENDS SET status = ? WHERE friend_id = ? AND user_id = ?";

    private static final String deleteFriendsSqlString =
            "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";

    private static final String findFriendsStatusSqlString =
            "SELECT status FROM FRIENDS WHERE user_id = ? AND friend_id = ?";

    @Override
    public Friends save(Friends friends) {
        int userId = friends.getUserId();
        int friendId = friends.getFriendId();
        String status = FriendsStatus.REQUESTED.name();
        try {
            if (jdbcTemplate.update(saveFriendsSqlString, userId, status, friendId) > 0) {
                return friends;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql insert for friends (" + userId + ", " + friendId + ").";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Friends update(Friends friends) {
        int userId = friends.getUserId();
        int friendId = friends.getFriendId();
        String status = friends.getStatus().name();
        try {
            if (jdbcTemplate.update(updateFriendsSqlString, status, friendId, userId) > 0) {
                return friends;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql insert for friends (" + userId + ", " + friendId + ").";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Friends delete(Friends friends) {
        int userId = friends.getUserId();
        int friendId = friends.getFriendId();
        try {
            if (jdbcTemplate.update(deleteFriendsSqlString, userId, friendId) > 0) {
                return friends;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for friends (" + userId + ", " + friendId + ").";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Friends findById(Friends friends) {
        int userId = friends.getUserId();
        int friendId = friends.getFriendId();
        try {
            String statusString = jdbcTemplate.queryForObject(
                    findFriendsStatusSqlString,
                    String.class,
                    userId,
                    friendId);
            return friends.withStatus(FriendsStatus.valueOf(statusString));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            String mes = "Error when execute sql select for friends (" + userId + ", " + friendId + ").";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public Collection<Friends> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Friends> findByIds(Collection<Friends> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Friends> findFirstNTopRows(Integer n) {
        throw new UnsupportedOperationException();
    }
}
