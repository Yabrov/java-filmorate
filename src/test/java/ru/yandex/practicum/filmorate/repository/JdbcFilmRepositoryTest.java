package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcFilmRepositoryTest extends AbstractRepositoryTest<Integer, Film> {

    private final AbstractRepository<Likes, Likes> likesRepository;
    private final AbstractRepository<Integer, User> userRepository;

    @Autowired
    public JdbcFilmRepositoryTest(
            @Qualifier("jdbcFilmRepository") AbstractRepository<Integer, Film> repository,
            @Qualifier("jdbcLikesRepository") AbstractRepository<Likes, Likes> likesRepository,
            @Qualifier("jdbcUserRepository") AbstractRepository<Integer, User> userRepository
    ) {
        super(repository);
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        entity = Film.builder()
                .id(null)
                .name("Film test name")
                .description("Film test descr")
                .duration(120)
                .releaseDate(LocalDate.of(2022, 11, 1))
                .rating(new Rating(2, "PG"))
                .genres(new HashSet<>() {
                    {
                        add(new Genre(1, "Комедия"));
                        add(new Genre(3, "Мультфильм"));
                        add(new Genre(5, "Документальный"));
                    }
                })
                .build();
    }

    @Test
    @Override
    @DisplayName("Тест вставки нового фильма в базу")
    void insertTest() throws JdbcQueryExecutionException {
        persistEntity(entity, 1);
    }

    @Test
    @Override
    @DisplayName("Тест обновления фильма")
    void updateTest() throws JdbcQueryExecutionException {
        Film createdFilm = persistEntity(entity, 1);
        String updatedFilmName = "Updated film name";
        Film updatedFilm = repository.update(createdFilm.withName(updatedFilmName));
        assertEquals(updatedFilmName, updatedFilm.getName(), "Фильм не был обновлен.");
        Film fetchedFilm = repository.findById(updatedFilm.getId());
        assertEquals(updatedFilm, fetchedFilm, "Обновленный фильм не соответствует сохраненному.");
    }

    @Test
    @Override
    @DisplayName("Тест удаления фильма из базы")
    void deleteTest() throws JdbcQueryExecutionException {
        Film createdFilm = persistEntity(entity, 1);
        Film deletedFilm = repository.delete(createdFilm);
        assertNull(repository.findById(deletedFilm.getId()), "Фильм не удален.");
    }

    @Test
    @Override
    @DisplayName("Тест получения фильма по id")
    void getByIdTest() throws JdbcQueryExecutionException {
        Film createdFilm = persistEntity(entity, 1);
        Film fetchedFilm = repository.findById(createdFilm.getId());
        assertEquals(createdFilm, fetchedFilm, "Полученный фильм не совпадает сохраненному.");
    }

    @Test
    @Override
    @DisplayName("Тест получения всех фильмов")
    void getAllTest() throws JdbcQueryExecutionException {
        for (int i = 1; i <= 10; i++) {
            persistEntity(entity.withName("test name" + i), i);
        }
        Collection<Film> users = repository.findAll();
        assertEquals(10, users.size(), "Фильмы не получены");
    }

    @Test
    @Override
    @DisplayName("Тест получения самых популярных фильмов")
    void findFirstNTopRowsTest() {
        for (int i = 1; i <= 10; i++) {
            persistEntity(entity.withName("test name" + i), i);
        }
        for (int i = 1; i <= 10; i++) {
            userRepository.save(User.builder()
                    .id(null)
                    .login("login")
                    .name("username")
                    .email("test@domain.xxx")
                    .birthday(LocalDate.of(1980, 1, 1))
                    .build());
        }
        for (int i = 1; i <= 10; i++) {
            for (int j = 10; j > i; j--) {
                likesRepository.save(new Likes(j, i));
            }
        }
        int[] nTopFilms = repository
                .findFirstNTopRows(3)
                .stream()
                .mapToInt(Film::getId)
                .toArray();
        int[] expectedIds = new int[]{1, 2, 3};
        assertArrayEquals(expectedIds, nTopFilms, "Полученный список неверный.");
    }

    @Override
    protected Film persistEntity(Film entity, Integer expectedId) throws JdbcQueryExecutionException {
        Film createdFilm = repository.save(entity);
        assertEquals(expectedId, createdFilm.getId(), "Новый фильм не был сохранен.");
        return createdFilm;
    }
}
