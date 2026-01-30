package com.example.gym.dao;

import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.model.Trainee;
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
class TraineeDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TraineeDao traineeDao;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("abc123xyz0");
        trainee.setDateOfBirth("1990-05-15");
        trainee.setAddress("123 Main St");
        trainee.setActive(true);
    }

    @Nested
    @DisplayName("Save operations")
    class SaveTests {

        @Test
        @DisplayName("Should generate ID when null")
        void saveShouldGenerateIdWhenIdIsNull() {
            trainee.setUserId(null);
            when(storage.generateId(Trainee.class)).thenReturn(5L);
            doNothing().when(storage).put(eq(Trainee.class), anyLong(), any());

            Trainee result = traineeDao.save(trainee);

            assertEquals(5L, result.getUserId());
            verify(storage).generateId(Trainee.class);
        }

        @Test
        @DisplayName("Should not generate ID when already set")
        void saveShouldNotGenerateIdWhenIdExists() {
            doNothing().when(storage).put(eq(Trainee.class), anyLong(), any());

            traineeDao.save(trainee);

            verify(storage, never()).generateId(any());
        }

        @Test
        @DisplayName("Should save trainee and return it")
        void saveShouldStoreTraineeAndReturn() {
            doNothing().when(storage).put(Trainee.class, 1L, trainee);

            Trainee result = traineeDao.save(trainee);

            assertNotNull(result);
            assertEquals(trainee, result);
            verify(storage, times(1)).put(Trainee.class, 1L, trainee);
        }
    }

    @Nested
    @DisplayName("FindById operations")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainee when found")
        void findByIdShouldReturnTraineeWhenExists() {
            when(storage.<Trainee>get(Trainee.class, 1L)).thenReturn(Optional.of(trainee));

            Trainee result = traineeDao.findById(1L);

            assertNotNull(result);
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals("John.Doe", result.getUsername());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when trainee not found")
        void findByIdShouldThrowExceptionWhenNotExists() {
            when(storage.<Trainee>get(Trainee.class, 999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> traineeDao.findById(999L)
            );

            assertTrue(exception.getMessage().contains("Trainee"));
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("FindByCondition operations")
    class FindByConditionTests {

        @Test
        @DisplayName("Should return trainees matching condition")
        void findByConditionShouldReturnMatchingTrainees() {
            Trainee trainee2 = new Trainee();
            trainee2.setUserId(2L);
            trainee2.setFirstName("Jane");
            trainee2.setLastName("Smith");
            trainee2.setActive(true);

            when(storage.<Trainee>getByCondition(eq(Trainee.class), any()))
                    .thenReturn(List.of(trainee, trainee2));

            List<Trainee> result = traineeDao.findByCondition(t -> t.isActive());

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no trainees match")
        void findByConditionShouldReturnEmptyListWhenNoMatch() {
            when(storage.<Trainee>getByCondition(eq(Trainee.class), any()))
                    .thenReturn(List.of());

            List<Trainee> result = traineeDao.findByCondition(t -> false);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Delete operations")
    class DeleteTests {

        @Test
        @DisplayName("Should delete trainee by id")
        void deleteByIdShouldCallStorageDelete() {
            doNothing().when(storage).delete(Trainee.class, 1L);

            traineeDao.deleteById(1L);

            verify(storage, times(1)).delete(Trainee.class, 1L);
        }
    }

    @Nested
    @DisplayName("ExistsByUsername operations")
    class ExistsByUsernameTests {

        @Test
        @DisplayName("Should return true when username exists")
        void existsByUsernameShouldReturnTrueWhenUsernameExists() {
            when(storage.existsByUsername("John.Doe")).thenReturn(true);

            boolean result = traineeDao.existsByUsername("John.Doe");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when username does not exist")
        void existsByUsernameShouldReturnFalseWhenUsernameNotExists() {
            when(storage.existsByUsername("Unknown.User")).thenReturn(false);

            boolean result = traineeDao.existsByUsername("Unknown.User");

            assertFalse(result);
        }
    }
}
