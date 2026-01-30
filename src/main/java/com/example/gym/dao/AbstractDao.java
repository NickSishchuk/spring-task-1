package com.example.gym.dao;

import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.storage.InMemoryStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
public abstract class AbstractDao<T, ID> implements Dao<T, ID> {

    protected InMemoryStorage storage;
    private final Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    protected abstract ID getId(T entity);
    protected abstract void setId(T entity, Long id);

    @Override
    public T save(T entity) {
        if (getId(entity) == null) {
            Long newId = storage.generateId(entityClass);
            setId(entity, newId);
        }
        storage.put(entityClass, getId(entity), entity);
        log.info("Saved {}: {}", entityClass.getSimpleName(), getId(entity));
        return entity;
    }

    @Override
    public T findById(ID id) {
        return storage.<T>get(entityClass, id)
                .orElseThrow(() -> new EntityNotFoundException(entityClass.getSimpleName(), (Long) id));
    }

    @Override
    public List<T> findByCondition(Predicate<T> condition) {
        return storage.<T>getByCondition(entityClass, condition);
    }

    @Override
    public void deleteById(ID id) {
        storage.delete(entityClass, id);
        log.info("Deleted {} with id: {}", entityClass.getSimpleName(), id);
    }

    public boolean existsByUsername(String username) {
        return storage.existsByUsername(username);
    }
}
