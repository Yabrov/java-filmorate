package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcRatingRepositoryTest extends AbstractRepositoryTest<Integer, Rating> {

    private final AbstractRepository<Integer, Film> filmRepository;

    public JdbcRatingRepositoryTest(
            @Qualifier("jdbcRatingRepository") AbstractRepository<Integer, Rating> repository,
            @Qualifier("jdbcFilmRepository") AbstractRepository<Integer, Film> filmRepository) {
        super(repository);
        this.filmRepository = filmRepository;
        this.entity = Rating.builder()
                .id(6)
                .name("XXX")
                .build();
    }

    @Test
    @Override
    @DisplayName("JDBC: Тест вставки нового рэйтинга в базу")
    void insertTest() throws JdbcQueryExecutionException {
        persistEntity(entity, entity.getId());
    }

    @Test
    @Override
    @DisplayName("JDBC: Тест обновления рэйтинга")
    void updateTest() throws JdbcQueryExecutionException {
        Rating createdRating = repository.findById(1);
        String updatedRatingName = "XXX updated";
        Rating updatedRating = repository.update(createdRating.withName(updatedRatingName));
        assertEquals(updatedRatingName, updatedRating.getName(), "Жанр не был обновлен.");
        Rating fetchedRating = repository.findById(updatedRating.getId());
        assertEquals(updatedRating, fetchedRating, "Обновленный рэйтинг не соответствует сохраненному.");
    }

    @Test
    @Override
    @DisplayName("JDBC: Тест удаления рэйтинга из базы")
    void deleteTest() throws JdbcQueryExecutionException {
        Rating createdRating = repository.findById(1);
        Rating deletedRating = repository.delete(createdRating);
        assertNull(repository.findById(deletedRating.getId()), "Жанр не удален.");
    }

    @Test
    @Override
    @DisplayName("JDBC: Тест получения рэйтинга по id")
    void getByIdTest() throws JdbcQueryExecutionException {
        Rating createdRating = repository.findById(1);
        Rating fetchedRating = repository.findById(createdRating.getId());
        assertEquals(createdRating, fetchedRating, "Полученный рэйтинг не совпадает сохраненному.");
    }

    @Test
    @Override
    @DisplayName("JDBC: Тест получения всех рэйтингов")
    void getAllTest() throws JdbcQueryExecutionException {
        Collection<Rating> users = repository.findAll();
        assertEquals(5, users.size(), "Жанры не получены");
    }

    @Test
    @Override
    @DisplayName("JDBC: Тест получения самых популярных рэйтингов")
    void findFirstNTopRowsTest() throws JdbcQueryExecutionException {
        for (int i = 1; i <= 6; i++) {
            Film film = Film.builder()
                    .id(null)
                    .name("Film test name")
                    .description("Film test descr")
                    .duration(120)
                    .releaseDate(LocalDate.of(2022, 11, 1))
                    .rating(new Rating(3, ""))
                    .build();
            filmRepository.save(film);
        }
        int[] nTopRatings = repository
                .findFirstNTopRows(1)
                .stream()
                .mapToInt(Rating::getId)
                .toArray();
        int[] expectedIds = new int[]{3};
        assertArrayEquals(expectedIds, nTopRatings, "Полученный список неверный.");
    }

    @Override
    protected Rating persistEntity(Rating entity, Integer expectedId) throws JdbcQueryExecutionException {
        Rating createdRating = repository.save(entity);
        assertEquals(expectedId, createdRating.getId(), "Новый рэйтинг не был сохранен.");
        return createdRating;
    }
}
