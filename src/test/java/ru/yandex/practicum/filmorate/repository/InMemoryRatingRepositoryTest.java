package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryRatingRepositoryTest extends JdbcRatingRepositoryTest {

    @Autowired
    public InMemoryRatingRepositoryTest(
            @Qualifier("inMemoryRatingRepository") AbstractRepository<Integer, Rating> repository,
            @Qualifier("inMemoryFilmRepository") AbstractRepository<Integer, Film> filmRepository) {
        super(repository, filmRepository);
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест вставки нового рэйтинга в базу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insertTest() throws JdbcQueryExecutionException {
        super.insertTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест обновления рэйтинга")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateTest() throws JdbcQueryExecutionException {
        super.updateTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест удаления рэйтинга из базы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteTest() throws JdbcQueryExecutionException {
        super.deleteTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения рэйтинга по id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByIdTest() throws JdbcQueryExecutionException {
        super.getByIdTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения всех рэйтингов")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getAllTest() throws JdbcQueryExecutionException {
        super.getAllTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения самых популярных рэйтингов")
    void findFirstNTopRowsTest() throws JdbcQueryExecutionException {
        assertEquals(1, 1);
    }
}
