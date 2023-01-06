package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUserRepositoryTest extends AbstractRepositoryTest<Integer, User> {

    private final AbstractRepository<Friends, Friends> friendsRepository;

    @Autowired
    public JdbcUserRepositoryTest(
            @Qualifier("jdbcUserRepository") AbstractRepository<Integer, User> repository,
            @Qualifier("jdbcFriendsRepository") AbstractRepository<Friends, Friends> friendsRepository
    ) {
        super(repository);
        this.friendsRepository = friendsRepository;
        entity = User.builder()
                .id(null)
                .login("login")
                .name("username")
                .email("test@domain.xxx")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
    }

    @Test
    @Override
    @DisplayName("Тест вставки нового пользователя в базу")
    void insertTest() throws JdbcQueryExecutionException {
        persistEntity(entity, 1);
    }

    @Test
    @Override
    @DisplayName("Тест обновления пользователя")
    void updateTest() throws JdbcQueryExecutionException {
        User createdUser = persistEntity(entity, 1);
        String updatedUserName = "Updated user name";
        User updatedUser = repository.update(createdUser.withName(updatedUserName));
        assertEquals(updatedUserName, updatedUser.getName(), "Пользователь не был обновлен.");
        User fetchedUser = repository.findById(updatedUser.getId());
        assertEquals(updatedUser, fetchedUser, "Обновленный пользователь не соответствует сохраненному.");
    }

    @Test
    @Override
    @DisplayName("Тест удаления пользователя из базы")
    void deleteTest() throws JdbcQueryExecutionException {
        User createdUser = persistEntity(entity, 1);
        User deletedUser = repository.delete(createdUser);
        assertNull(repository.findById(deletedUser.getId()), "Пользователь не удален.");
    }

    @Test
    @Override
    @DisplayName("Тест получения пользователя по id")
    void getByIdTest() throws JdbcQueryExecutionException {
        User createdUser = persistEntity(entity, 1);
        User fetchedUser = repository.findById(createdUser.getId());
        assertEquals(createdUser, fetchedUser, "Полученный пользователь не совпадает сохраненному.");
    }

    @Test
    @Override
    @DisplayName("Тест получения всех пользователей")
    void getAllTest() throws JdbcQueryExecutionException {
        for (int i = 1; i <= 10; i++) {
            persistEntity(entity.withName("test name" + i), i);
        }
        Collection<User> users = repository.findAll();
        assertEquals(10, users.size(), "Пользователи не получены");
    }

    @Test
    @Override
    @DisplayName("Тест получения самых популярных пользователей")
    void findFirstNTopRowsTest() {
        for (int i = 1; i <= 10; i++) {
            persistEntity(entity.withName("test name" + i), i);
        }
        for (int i = 1; i <= 10; i++) {
            for (int j = 10; j > i; j--) {
                friendsRepository.save(new Friends(i, j, FriendsStatus.REQUESTED));
            }
        }
        int[] nTopUsers = repository
                .findFirstNTopRows(3)
                .stream()
                .mapToInt(User::getId)
                .toArray();
        int[] expectedIds = new int[]{1, 2, 3};
        assertArrayEquals(expectedIds, nTopUsers, "Полученный список неверный.");
    }

    @Override
    protected User persistEntity(User entity, Integer expectedId) throws JdbcQueryExecutionException {
        User createdUser = repository.save(entity);
        assertEquals(expectedId, createdUser.getId(), "Новый пользователь не был сохранен.");
        return createdUser;
    }
}
