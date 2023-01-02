package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface AbstractRepository<R, T> {

    T save(T t);

    T update(T t);

    T delete(T t);

    T findById(R id);

    Collection<T> findAll();

    Collection<T> findByIds(Collection<R> ids);
}
