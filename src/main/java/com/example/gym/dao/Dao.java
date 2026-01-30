package com.example.gym.dao;

import java.util.List;
import java.util.function.Predicate;

public interface Dao<T, ID> {
    T save(T entity);
    T findById(ID id);
    List<T> findByCondition(Predicate<T> condition);
    void deleteById(ID id);
}
