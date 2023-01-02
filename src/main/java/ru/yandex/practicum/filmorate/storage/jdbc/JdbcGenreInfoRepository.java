package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.GenreInfo;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Component("jdbcGenreInfoRepository")
public class JdbcGenreInfoRepository implements AbstractRepository<GenreInfo, GenreInfo> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<GenreInfo> genreInfoMapper;

    @Override
    public GenreInfo save(GenreInfo genreInfo) {
        return null;
    }

    @Override
    public GenreInfo update(GenreInfo genreInfo) {
        return null;
    }

    @Override
    public GenreInfo delete(GenreInfo genreInfo) {
        return null;
    }

    @Override
    public GenreInfo findById(GenreInfo id) {
        return null;
    }

    @Override
    public Collection<GenreInfo> findAll() {
        return null;
    }

    @Override
    public Collection<GenreInfo> findByIds(Collection<GenreInfo> ids) {
        return null;
    }
}
