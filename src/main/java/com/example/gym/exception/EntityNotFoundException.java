package com.example.gym.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " not found with id: " + id);
    }
}
