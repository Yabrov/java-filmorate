package ru.yandex.practicum.filmorate.storage.jdbc.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Likes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikesMapper implements RowMapper<Likes> {

    @Override
    public Likes mapRow(ResultSet rs, int rowNum) throws SQLException {
        int userId = rs.getInt("user_id");
        int filmId = rs.getInt("film_id");
        return new Likes(userId, filmId);
    }
}
