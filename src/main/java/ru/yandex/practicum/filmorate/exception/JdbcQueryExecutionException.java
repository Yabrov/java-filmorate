package ru.yandex.practicum.filmorate.exception;

public class JdbcQueryExecutionException extends RuntimeException {

    public JdbcQueryExecutionException(String mes, Exception e) {
        super(mes, e);
    }
}
