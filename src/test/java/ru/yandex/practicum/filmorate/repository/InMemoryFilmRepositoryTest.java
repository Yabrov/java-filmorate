package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

public class InMemoryFilmRepositoryTest extends JdbcFilmRepositoryTest {

    @Autowired
    public InMemoryFilmRepositoryTest(
            @Qualifier("inMemoryFilmRepository") AbstractRepository<Integer, Film> repository,
            @Qualifier("inMemoryLikesRepository") AbstractRepository<Likes, Likes> likesRepository,
            @Qualifier("inMemoryUserRepository") AbstractRepository<Integer, User> userRepository) {
        super(repository, likesRepository, userRepository);
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест вставки нового фильма в базу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insertTest() throws JdbcQueryExecutionException {
        super.insertTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест обновления фильма")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateTest() throws JdbcQueryExecutionException {
        super.updateTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест удаления фильма из базы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteTest() throws JdbcQueryExecutionException {
        super.deleteTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения фильма по id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByIdTest() throws JdbcQueryExecutionException {
        super.getByIdTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения всех фильмов")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getAllTest() throws JdbcQueryExecutionException {
        super.getAllTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения самых популярных фильмов")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void findFirstNTopRowsTest() {
        super.findFirstNTopRowsTest();
    }
}
