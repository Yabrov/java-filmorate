package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcGenreRepositoryTest extends AbstractRepositoryTest<Integer, Genre> {

    private final AbstractRepository<Integer, Film> filmRepository;

    public JdbcGenreRepositoryTest(
            @Qualifier("jdbcGenreRepository") AbstractRepository<Integer, Genre> repository,
            @Qualifier("jdbcFilmRepository") AbstractRepository<Integer, Film> filmRepository) {
        super(repository);
        this.filmRepository = filmRepository;
        this.entity = Genre.builder()
                .id(7)
                .name("XXX")
                .build();
    }

    @Test
    @Override
    @DisplayName("Тест вставки нового жанра в базу")
    void insertTest() throws JdbcQueryExecutionException {
        persistEntity(entity, entity.getId());
    }

    @Test
    @Override
    @DisplayName("Тест обновления жанра")
    void updateTest() throws JdbcQueryExecutionException {
        Genre createdGenre = repository.findById(1);
        String updatedGenreName = "XXX updated";
        Genre updatedGenre = repository.update(createdGenre.withName(updatedGenreName));
        assertEquals(updatedGenreName, updatedGenre.getName(), "Жанр не был обновлен.");
        Genre fetchedGenre = repository.findById(updatedGenre.getId());
        assertEquals(updatedGenre, fetchedGenre, "Обновленный жанр не соответствует сохраненному.");
    }

    @Test
    @Override
    @DisplayName("Тест удаления жанра из базы")
    void deleteTest() throws JdbcQueryExecutionException {
        Genre createdGenre = repository.findById(1);
        Genre deletedGenre = repository.delete(createdGenre);
        assertNull(repository.findById(deletedGenre.getId()), "Жанр не удален.");
    }

    @Test
    @Override
    @DisplayName("Тест получения жанра по id")
    void getByIdTest() throws JdbcQueryExecutionException {
        Genre createdGenre = repository.findById(1);
        Genre fetchedGenre = repository.findById(createdGenre.getId());
        assertEquals(createdGenre, fetchedGenre, "Полученный жанр не совпадает сохраненному.");
    }
    @Test
    @Override
    @DisplayName("Тест получения всех жанров")    
    void getAllTest() throws JdbcQueryExecutionException {
        Collection<Genre> users = repository.findAll();
        assertEquals(6, users.size(), "Жанры не получены");
    }

    @Test
    @Override
    @DisplayName("Тест получения самых популярных жанров")
    void findFirstNTopRowsTest() throws JdbcQueryExecutionException {
        for (int i = 1; i <= 6; i++) {
            Film film = Film.builder()
                    .id(null)
                    .name("Film test name")
                    .description("Film test descr")
                    .duration(120)
                    .releaseDate(LocalDate.of(2022, 11, 1))
                    .rating(new Rating(2, "PG"))
                    .build();
            for (int j = 6; j > i; j--) {
                film.getGenres().add(new Genre(j, ""));
            }
            filmRepository.save(film);
        }
        int[] nTopGenres = repository
                .findFirstNTopRows(1)
                .stream()
                .mapToInt(Genre::getId)
                .toArray();
        int[] expectedIds = new int[]{6};
        assertArrayEquals(expectedIds, nTopGenres, "Полученный список неверный.");
    }

    @Override
    protected Genre persistEntity(Genre entity, Integer expectedId) throws JdbcQueryExecutionException {
        Genre createdGenre = repository.save(entity);
        assertEquals(expectedId, createdGenre.getId(), "Новый жанр не был сохранен.");
        return createdGenre;
    }
}
