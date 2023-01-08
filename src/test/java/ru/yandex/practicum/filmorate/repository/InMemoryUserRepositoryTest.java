package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

public class InMemoryUserRepositoryTest extends JdbcUserRepositoryTest {

    @Autowired
    public InMemoryUserRepositoryTest(
            @Qualifier("inMemoryUserRepository") AbstractRepository<Integer, User> repository,
            @Qualifier("inMemoryFriendsRepository") AbstractRepository<Friends, Friends> friendsRepository) {
        super(repository, friendsRepository);
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест вставки нового пользователя в базу")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insertTest() throws JdbcQueryExecutionException {
        super.insertTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест обновления пользователя")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateTest() throws JdbcQueryExecutionException {
        super.updateTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест удаления пользователя из базы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteTest() throws JdbcQueryExecutionException {
        super.deleteTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения пользователя по id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByIdTest() throws JdbcQueryExecutionException {
        super.getByIdTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения всех пользователей")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getAllTest() throws JdbcQueryExecutionException {
        super.getAllTest();
    }

    @Test
    @Override
    @DisplayName("InMemory: Тест получения самых популярных пользователей")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void findFirstNTopRowsTest() {
        super.findFirstNTopRowsTest();
    }
}
