package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface AbstractRepository<T> {

    T save(T t);

    T update(T t);

    T delete(T t);

    T findById(Integer id);

    Collection<T> findAll();
}
