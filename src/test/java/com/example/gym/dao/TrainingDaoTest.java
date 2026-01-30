package com.example.gym.dao;

import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.model.Training;
import com.example.gym.model.TrainingType;
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
class TrainingDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainingDao trainingDao;

    private Training training;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = TrainingType.builder()
                .trainingTypeId(1L)
                .trainingTypeName("Cardio")
                .build();

        training = Training.builder()
                .trainingId(1L)
                .traineeId(1L)
                .trainerId(1L)
                .trainingName("Morning Cardio")
                .trainingType(trainingType)
                .trainingDate("2024-01-15")
                .trainingDuration(60)
                .build();
    }

    @Nested
    @DisplayName("Save operations")
    class SaveTests {

        @Test
        @DisplayName("Should generate ID when null")
        void saveShouldGenerateIdWhenIdIsNull() {
            training = Training.builder()
                    .trainingId(null)
                    .traineeId(1L)
                    .trainerId(1L)
                    .trainingName("Morning Cardio")
                    .build();
            when(storage.generateId(Training.class)).thenReturn(5L);
            doNothing().when(storage).put(eq(Training.class), anyLong(), any());

            Training result = trainingDao.save(training);

            assertEquals(5L, result.getTrainingId());
            verify(storage).generateId(Training.class);
        }

        @Test
        @DisplayName("Should not generate ID when already set")
        void saveShouldNotGenerateIdWhenIdExists() {
            doNothing().when(storage).put(eq(Training.class), anyLong(), any());

            trainingDao.save(training);

            verify(storage, never()).generateId(any());
        }

        @Test
        @DisplayName("Should save training and return it")
        void saveShouldStoreTrainingAndReturn() {
            doNothing().when(storage).put(Training.class, 1L, training);

            Training result = trainingDao.save(training);

            assertNotNull(result);
            assertEquals(training, result);
            verify(storage, times(1)).put(Training.class, 1L, training);
        }

        @Test
        @DisplayName("Should preserve all training fields on save")
        void saveShouldPreserveAllFields() {
            doNothing().when(storage).put(eq(Training.class), anyLong(), any());

            Training result = trainingDao.save(training);

            assertEquals("Morning Cardio", result.getTrainingName());
            assertEquals(1L, result.getTraineeId());
            assertEquals(1L, result.getTrainerId());
            assertEquals("2024-01-15", result.getTrainingDate());
            assertEquals(60, result.getTrainingDuration());
            assertEquals("Cardio", result.getTrainingType().getTrainingTypeName());
        }
    }

    @Nested
    @DisplayName("FindById operations")
    class FindByIdTests {

        @Test
        @DisplayName("Should return training when found")
        void findByIdShouldReturnTrainingWhenExists() {
            when(storage.<Training>get(Training.class, 1L)).thenReturn(Optional.of(training));

            Training result = trainingDao.findById(1L);

            assertNotNull(result);
            assertEquals("Morning Cardio", result.getTrainingName());
            assertEquals(1L, result.getTraineeId());
            assertEquals(1L, result.getTrainerId());
        }

        @Test
        @DisplayName("Should return training with nested TrainingType")
        void findByIdShouldReturnTrainingWithNestedType() {
            when(storage.<Training>get(Training.class, 1L)).thenReturn(Optional.of(training));

            Training result = trainingDao.findById(1L);

            assertNotNull(result.getTrainingType());
            assertEquals("Cardio", result.getTrainingType().getTrainingTypeName());
            assertEquals(1L, result.getTrainingType().getTrainingTypeId());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when training not found")
        void findByIdShouldThrowExceptionWhenNotExists() {
            when(storage.<Training>get(Training.class, 999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> trainingDao.findById(999L)
            );

            assertTrue(exception.getMessage().contains("Training"));
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("FindByCondition operations")
    class FindByConditionTests {

        @Test
        @DisplayName("Should return trainings matching condition")
        void findByConditionShouldReturnMatchingTrainings() {
            TrainingType yogaType = TrainingType.builder()
                    .trainingTypeId(2L)
                    .trainingTypeName("Yoga")
                    .build();

            Training training2 = Training.builder()
                    .trainingId(2L)
                    .traineeId(2L)
                    .trainerId(2L)
                    .trainingName("Evening Yoga")
                    .trainingType(yogaType)
                    .trainingDate("2024-01-16")
                    .trainingDuration(45)
                    .build();

            when(storage.<Training>getByCondition(eq(Training.class), any()))
                    .thenReturn(List.of(training, training2));

            List<Training> result = trainingDao.findByCondition(t -> true);

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(t -> t.getTrainingName().equals("Morning Cardio")));
            assertTrue(result.stream().anyMatch(t -> t.getTrainingName().equals("Evening Yoga")));
        }

        @Test
        @DisplayName("Should return empty list when no trainings match")
        void findByConditionShouldReturnEmptyListWhenNoMatch() {
            when(storage.<Training>getByCondition(eq(Training.class), any()))
                    .thenReturn(List.of());

            List<Training> result = trainingDao.findByCondition(t -> false);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Delete operations")
    class DeleteTests {

        @Test
        @DisplayName("Should delete training by id")
        void deleteByIdShouldCallStorageDelete() {
            doNothing().when(storage).delete(Training.class, 1L);

            trainingDao.deleteById(1L);

            verify(storage, times(1)).delete(Training.class, 1L);
        }
    }
}
