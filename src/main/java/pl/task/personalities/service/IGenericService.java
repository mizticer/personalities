package pl.task.personalities.service;

import java.util.List;

public interface IGenericService<T> {
    T add(T object);

    T get(long id);

    T edit(T object);

    void delete(long id);

    List<T> getAll();
}
