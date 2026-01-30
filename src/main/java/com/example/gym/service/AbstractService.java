package com.example.gym.service;

import com.example.gym.dao.Dao;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
public abstract class AbstractService<T, ID, D extends Dao<T, ID>> {

    protected D dao;

    protected void setDao(D dao) {
        this.dao = dao;
    }

    public T select(ID id) {
        log.debug("Selecting entity with id: {}", id);
        return dao.findById(id);
    }

    public void delete(ID id) {
        log.debug("Deleting entity with id: {}", id);
        dao.deleteById(id);
    }

    public List<T> findByCondition(Predicate<T> condition) {
        log.debug("Finding entities by condition");
        return dao.findByCondition(condition);
    }
}
