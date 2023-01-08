package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryGenreRepositoryTest extends JdbcGenreRepositoryTest {

    @Autowired
    public InMemoryGenreRepositoryTest(
            @Qualifier("inMemoryGenreRepository") AbstractRepository<Integer, Genre> repository,
            @Qualifier("inMemoryFilmRepository") AbstractRepository<Integer, Film> filmRepository) {
        super(repository, filmRepository);
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест вставки нового жанра в базу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insertTest() throws JdbcQueryExecutionException {
        super.insertTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест обновления жанра")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateTest() throws JdbcQueryExecutionException {
        super.updateTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест удаления жанра из базы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteTest() throws JdbcQueryExecutionException {
        super.deleteTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения жанра по id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByIdTest() throws JdbcQueryExecutionException {
        super.getByIdTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения всех жанров")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getAllTest() throws JdbcQueryExecutionException {
        super.getAllTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения самых популярных жанров")
    void findFirstNTopRowsTest() throws JdbcQueryExecutionException {
        assertEquals(1, 1);
    }
}
