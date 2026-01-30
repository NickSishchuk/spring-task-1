package com.example.gym.storage;

import com.example.gym.model.Trainee;
import com.example.gym.model.Trainer;
import com.example.gym.model.Training;
import com.example.gym.model.TrainingType;
import com.example.gym.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Component
@Slf4j
public class InMemoryStorage {

    private final Map<String, Object> storage = new ConcurrentHashMap<>();
    private final Map<Class<?>, Long> idCounters = new ConcurrentHashMap<>();

    private String getNamespace(Class<?> entityClass) {
        return entityClass.getSimpleName().toLowerCase();
    }

    private String buildKey(Class<?> entityClass, Object id) {
        return getNamespace(entityClass) + ":" + id;
    }

    public Long generateId(Class<?> entityClass) {
        return idCounters.merge(entityClass, 1L, Long::sum);
    }

    public void initializeIdCounter(Class<?> entityClass, Long id) {
        idCounters.merge(entityClass, id, Long::max);
    }

    public void put(Class<?> entityClass, Object id, Object entity) {
        String key = buildKey(entityClass, id);
        storage.put(key, entity);
        log.debug("Saved entity with key: {}", key);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Class<T> entityClass, Object id) {
        String key = buildKey(entityClass, id);
        Object entity = storage.get(key);
        if (entity != null && entityClass.isInstance(entity)) {
            return Optional.of((T) entity);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getByCondition(Class<T> entityClass, Predicate<T> condition) {
        String namespace = getNamespace(entityClass);
        return storage.entrySet().stream()
                .filter(e -> e.getKey().startsWith(namespace + ":"))
                .map(Map.Entry::getValue)
                .filter(entityClass::isInstance)
                .map(obj -> (T) obj)
                .filter(condition)
                .toList();
    }

    public void delete(Class<?> entityClass, Object id) {
        String key = buildKey(entityClass, id);
        storage.remove(key);
        log.debug("Deleted entity with key: {}", key);
    }

    public boolean existsByUsername(String username) {
        return storage.values().stream()
                .filter(obj -> obj instanceof User)
                .map(obj -> (User) obj)
                .anyMatch(user -> username.equals(user.getUsername()));
    }

    public int size() {
        return storage.size();
    }

    public void clear() {
        storage.clear();
        idCounters.clear();
    }
}
