package ru.yandex.practicum.filmorate.storage.jdbc.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.utils.LocalDateConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        int ratingId = rs.getInt("rating_id");
        String ratingName = rs.getString("rating_name");
        LocalDate releaseDate = LocalDateConvertor
                .fromSqlString(rs.getString("release_date"));
        Rating rating = null;
        if (ratingId != 0 || ratingName != null) {
            rating = new Rating(ratingId, ratingName);
        }
        return new Film(id, name, description, releaseDate, duration, rating, null);
    }
}
