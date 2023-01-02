package ru.yandex.practicum.filmorate.storage.jdbc.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.GenreInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreInfoMapper implements RowMapper<GenreInfo> {

    @Override
    public GenreInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("film_id");
        int genreId = rs.getInt("genre_id");
        return new GenreInfo(filmId, genreId);
    }
}
