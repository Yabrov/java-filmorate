package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.JdbcQueryExecutionException;
import ru.yandex.practicum.filmorate.storage.AbstractRepository;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = {"classpath:schema.sql", "classpath:data.sql"}
)
@ComponentScan("ru.yandex.practicum.filmorate")
public abstract class AbstractRepositoryTest<I, T> {

    protected AbstractRepository<I, T> repository;

    protected T entity;

    protected AbstractRepositoryTest(AbstractRepository<I, T> repository) {
        this.repository = repository;
    }

    @Test
    abstract void insertTest() throws JdbcQueryExecutionException;

    @Test
    abstract void updateTest() throws JdbcQueryExecutionException;

    @Test
    abstract void deleteTest() throws JdbcQueryExecutionException;

    @Test
    abstract void getByIdTest() throws JdbcQueryExecutionException;

    @Test
    abstract void getAllTest() throws JdbcQueryExecutionException;

    @Test
    abstract void findFirstNTopRowsTest() throws JdbcQueryExecutionException;

    protected abstract T persistEntity(T entity, I expectedId) throws JdbcQueryExecutionException;
}
