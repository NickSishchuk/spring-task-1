package com.example.gym.storage;

import com.example.gym.model.Trainee;
import com.example.gym.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageTest {

    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
    }

    @Test
    @DisplayName("GenerateId returns 1 for first call")
    void generateIdShouldReturnOneForFirstCall() {
        Long id = storage.generateId(Trainee.class);

        assertEquals(1L, id);
    }

    @Test
    @DisplayName("GenerateId increments on each call")
    void generateIdShouldIncrementOnEachCall() {
        Long first = storage.generateId(Trainee.class);
        Long second = storage.generateId(Trainee.class);
        Long third = storage.generateId(Trainee.class);

        assertEquals(1L, first);
        assertEquals(2L, second);
        assertEquals(3L, third);
    }

    @Test
    @DisplayName("GenerateId tracks separately per entity class")
    void generateIdShouldTrackSeparatelyPerEntityClass() {
        Long traineeId1 = storage.generateId(Trainee.class);
        Long traineeId2 = storage.generateId(Trainee.class);
        Long trainerId1 = storage.generateId(Trainer.class);

        assertEquals(1L, traineeId1);
        assertEquals(2L, traineeId2);
        assertEquals(1L, trainerId1);
    }

    @Test
    @DisplayName("Put and get trainee works correctly")
    void putAndGetShouldStoreAndRetrieveEntity() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        storage.put(Trainee.class, 1L, trainee);
        Optional<Trainee> result = storage.get(Trainee.class, 1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    @DisplayName("Get returns empty if entity not found")
    void getShouldReturnEmptyIfNotFound() {
        Optional<Trainee> result = storage.get(Trainee.class, 999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Different entity classes store separately")
    void putShouldStoreSeparatelyByEntityClass() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");

        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setFirstName("Jane");

        storage.put(Trainee.class, 1L, trainee);
        storage.put(Trainer.class, 1L, trainer);

        Optional<Trainee> traineeResult = storage.get(Trainee.class, 1L);
        Optional<Trainer> trainerResult = storage.get(Trainer.class, 1L);

        assertTrue(traineeResult.isPresent());
        assertTrue(trainerResult.isPresent());
        assertEquals("John", traineeResult.get().getFirstName());
        assertEquals("Jane", trainerResult.get().getFirstName());
    }

    @Test
    @DisplayName("GetByCondition returns only entities matching condition")
    void getByConditionShouldReturnOnlyMatchingEntities() {
        Trainee trainee1 = new Trainee();
        trainee1.setUserId(1L);
        trainee1.setActive(true);

        Trainee trainee2 = new Trainee();
        trainee2.setUserId(2L);
        trainee2.setActive(false);

        storage.put(Trainee.class, 1L, trainee1);
        storage.put(Trainee.class, 2L, trainee2);

        List<Trainee> activeTrainees = storage.getByCondition(Trainee.class, Trainee::isActive);

        assertEquals(1, activeTrainees.size());
        assertTrue(activeTrainees.get(0).isActive());
    }

    @Test
    @DisplayName("GetByCondition returns empty list when no entities match")
    void getByConditionShouldReturnEmptyListWhenNoMatch() {
        List<Trainee> result = storage.getByCondition(Trainee.class, t -> false);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Delete removes entity from storage")
    void deleteShouldRemoveEntity() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        storage.put(Trainee.class, 1L, trainee);

        storage.delete(Trainee.class, 1L);

        Optional<Trainee> result = storage.get(Trainee.class, 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Delete only removes from correct entity class")
    void deleteShouldOnlyAffectSpecifiedEntityClass() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);

        storage.put(Trainee.class, 1L, trainee);
        storage.put(Trainer.class, 1L, trainer);

        storage.delete(Trainee.class, 1L);

        assertTrue(storage.get(Trainee.class, 1L).isEmpty());
        assertTrue(storage.get(Trainer.class, 1L).isPresent());
    }

    @Test
    @DisplayName("ExistsByUsername returns true when username exists")
    void existsByUsernameShouldReturnTrueWhenExists() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setUsername("John.Doe");
        storage.put(Trainee.class, 1L, trainee);

        boolean result = storage.existsByUsername("John.Doe");

        assertTrue(result);
    }

    @Test
    @DisplayName("ExistsByUsername returns false when username not exists")
    void existsByUsernameShouldReturnFalseWhenNotExists() {
        boolean result = storage.existsByUsername("NonExistent.User");

        assertFalse(result);
    }

    @Test
    @DisplayName("ExistsByUsername finds username across entity classes")
    void existsByUsernameShouldFindAcrossEntityClasses() {
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setUsername("Jane.Smith");
        storage.put(Trainer.class, 1L, trainer);

        boolean result = storage.existsByUsername("Jane.Smith");

        assertTrue(result);
    }

    @Test
    @DisplayName("InitializeIdCounter sets counter correctly")
    void initializeIdCounterShouldSetCounterCorrectly() {
        storage.initializeIdCounter(Trainee.class, 10L);

        Long nextId = storage.generateId(Trainee.class);

        assertEquals(11L, nextId);
    }

    @Test
    @DisplayName("Size returns correct count")
    void sizeShouldReturnCorrectCount() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);

        storage.put(Trainee.class, 1L, trainee);
        storage.put(Trainer.class, 1L, trainer);

        assertEquals(2, storage.size());
    }

    @Test
    @DisplayName("Clear removes all entities")
    void clearShouldRemoveAllEntities() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        storage.put(Trainee.class, 1L, trainee);

        storage.clear();

        assertEquals(0, storage.size());
        assertTrue(storage.get(Trainee.class, 1L).isEmpty());
    }
}
