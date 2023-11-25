package pl.task.personalities.service;

import jakarta.persistence.EntityExistsException;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.task.personalities.common.Identification;
import pl.task.personalities.exceptions.EntityNotFoundException;

import java.util.List;

public class GenericService<T extends Identification, R extends JpaRepository<T, Long>> implements IGenericService<T> {
    protected R repository;

    public GenericService(R repository) {
        this.repository = repository;
    }

    @Override
    public T add(T object) {
        if (object.getId() != null) {
            throw new EntityExistsException("Entity exist");
        }
        return repository.save(object);
    }

    @Override
    public T get(long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    @Override
    public T edit(T object) {
        T loadedEntity = repository.findById(object.getId())
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return repository.save(loadedEntity);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }
}
