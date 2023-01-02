package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.FriendshipInfo;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class JdbcFriendshipInfoRepository implements AbstractRepository<FriendshipInfo, FriendshipInfo> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public FriendshipInfo save(FriendshipInfo friendshipInfo) {
        String queryString = "INSERT INTO public.friendship_info(user_id, friend_id) VALUES(?, ?)";
        int userId = friendshipInfo.getUserId();
        int friendId = friendshipInfo.getFriendId();
        try {
            if (jdbcTemplate.update(queryString, userId, friendId) > 0) {
                return friendshipInfo;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql insert for friends (" + userId + ", " + friendId + ").";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public FriendshipInfo update(FriendshipInfo friendshipInfo) {
        return friendshipInfo;
    }

    @Override
    public FriendshipInfo delete(FriendshipInfo friendshipInfo) {
        String queryString = "DELETE FROM public.friendship_info f WHERE f.user_id = ? AND f.friend_id = ?";
        int userId = friendshipInfo.getUserId();
        int friendId = friendshipInfo.getFriendId();
        try {
            if (jdbcTemplate.update(queryString, userId, friendId) > 0) {
                return friendshipInfo;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            String mes = "Error when execute sql delete for friends (" + userId + ", " + friendId + ").";
            throw new JdbcQueryExecutionException(mes, e);
        }
    }

    @Override
    public FriendshipInfo findById(FriendshipInfo id) {
        return null;
    }

    @Override
    public Collection<FriendshipInfo> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Collection<FriendshipInfo> findByIds(Collection<FriendshipInfo> ids) {
        return Collections.emptyList();
    }
}
