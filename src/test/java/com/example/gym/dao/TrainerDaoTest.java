package com.example.gym.dao;

import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.model.Trainer;
import com.example.gym.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainerDao trainerDao;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setTrainerId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("Jane.Smith");
        trainer.setPassword("xyz789abc1");
        trainer.setSpecialization("Cardio");
        trainer.setActive(true);
    }

    @Nested
    @DisplayName("Save operations")
    class SaveTests {

        @Test
        @DisplayName("Should generate ID when null")
        void saveShouldGenerateIdWhenIdIsNull() {
            trainer.setUserId(null);
            when(storage.generateId(Trainer.class)).thenReturn(5L);
            doNothing().when(storage).put(eq(Trainer.class), anyLong(), any());

            Trainer result = trainerDao.save(trainer);

            assertEquals(5L, result.getUserId());
            verify(storage).generateId(Trainer.class);
        }

        @Test
        @DisplayName("Should not generate ID when already set")
        void saveShouldNotGenerateIdWhenIdExists() {
            doNothing().when(storage).put(eq(Trainer.class), anyLong(), any());

            trainerDao.save(trainer);

            verify(storage, never()).generateId(any());
        }

        @Test
        @DisplayName("Should save trainer and return it")
        void saveShouldStoreTrainerAndReturn() {
            doNothing().when(storage).put(Trainer.class, 1L, trainer);

            Trainer result = trainerDao.save(trainer);

            assertNotNull(result);
            assertEquals(trainer, result);
            verify(storage, times(1)).put(Trainer.class, 1L, trainer);
        }

        @Test
        @DisplayName("Should save trainer with specialization")
        void saveShouldPreserveSpecialization() {
            doNothing().when(storage).put(eq(Trainer.class), anyLong(), any());

            Trainer result = trainerDao.save(trainer);

            assertEquals("Cardio", result.getSpecialization());
        }
    }

    @Nested
    @DisplayName("FindById operations")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainer when found")
        void findByIdShouldReturnTrainerWhenExists() {
            when(storage.<Trainer>get(Trainer.class, 1L)).thenReturn(Optional.of(trainer));

            Trainer result = trainerDao.findById(1L);

            assertNotNull(result);
            assertEquals("Jane", result.getFirstName());
            assertEquals("Smith", result.getLastName());
            assertEquals("Jane.Smith", result.getUsername());
            assertEquals("Cardio", result.getSpecialization());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when trainer not found")
        void findByIdShouldThrowExceptionWhenNotExists() {
            when(storage.<Trainer>get(Trainer.class, 999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> trainerDao.findById(999L)
            );

            assertTrue(exception.getMessage().contains("Trainer"));
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("FindByCondition operations")
    class FindByConditionTests {

        @Test
        @DisplayName("Should return trainers matching condition")
        void findByConditionShouldReturnMatchingTrainers() {
            Trainer trainer2 = new Trainer();
            trainer2.setUserId(2L);
            trainer2.setFirstName("Mike");
            trainer2.setLastName("Johnson");
            trainer2.setSpecialization("Strength");

            when(storage.<Trainer>getByCondition(eq(Trainer.class), any()))
                    .thenReturn(List.of(trainer, trainer2));

            List<Trainer> result = trainerDao.findByCondition(t -> true);

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(t -> t.getSpecialization().equals("Cardio")));
            assertTrue(result.stream().anyMatch(t -> t.getSpecialization().equals("Strength")));
        }

        @Test
        @DisplayName("Should return empty list when no trainers match")
        void findByConditionShouldReturnEmptyListWhenNoMatch() {
            when(storage.<Trainer>getByCondition(eq(Trainer.class), any()))
                    .thenReturn(List.of());

            List<Trainer> result = trainerDao.findByCondition(t -> false);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Delete operations")
    class DeleteTests {

        @Test
        @DisplayName("Should delete trainer by id")
        void deleteByIdShouldCallStorageDelete() {
            doNothing().when(storage).delete(Trainer.class, 1L);

            trainerDao.deleteById(1L);

            verify(storage, times(1)).delete(Trainer.class, 1L);
        }
    }

    @Nested
    @DisplayName("ExistsByUsername operations")
    class ExistsByUsernameTests {

        @Test
        @DisplayName("Should return true when username exists")
        void existsByUsernameShouldReturnTrueWhenUsernameExists() {
            when(storage.existsByUsername("Jane.Smith")).thenReturn(true);

            boolean result = trainerDao.existsByUsername("Jane.Smith");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when username does not exist")
        void existsByUsernameShouldReturnFalseWhenUsernameNotExists() {
            when(storage.existsByUsername("Unknown.User")).thenReturn(false);

            boolean result = trainerDao.existsByUsername("Unknown.User");

            assertFalse(result);
        }
    }
}
