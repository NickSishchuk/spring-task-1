package com.example.gym.dao;

import com.example.gym.exception.EntityNotFoundException;
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
class TrainingTypeDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainingTypeDao trainingTypeDao;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = TrainingType.builder()
                .trainingTypeId(1L)
                .trainingTypeName("Cardio")
                .build();
    }

    @Nested
    @DisplayName("Save operations")
    class SaveTests {

        @Test
        @DisplayName("Should generate ID when null")
        void saveShouldGenerateIdWhenIdIsNull() {
            trainingType = TrainingType.builder()
                    .trainingTypeId(null)
                    .trainingTypeName("Cardio")
                    .build();
            when(storage.generateId(TrainingType.class)).thenReturn(5L);
            doNothing().when(storage).put(eq(TrainingType.class), anyLong(), any());

            TrainingType result = trainingTypeDao.save(trainingType);

            assertEquals(5L, result.getTrainingTypeId());
            verify(storage).generateId(TrainingType.class);
        }

        @Test
        @DisplayName("Should not generate ID when already set")
        void saveShouldNotGenerateIdWhenIdExists() {
            doNothing().when(storage).put(eq(TrainingType.class), anyLong(), any());

            trainingTypeDao.save(trainingType);

            verify(storage, never()).generateId(any());
        }

        @Test
        @DisplayName("Should save training type and return it")
        void saveShouldStoreTrainingTypeAndReturn() {
            doNothing().when(storage).put(TrainingType.class, 1L, trainingType);

            TrainingType result = trainingTypeDao.save(trainingType);

            assertNotNull(result);
            assertEquals(trainingType, result);
            verify(storage, times(1)).put(TrainingType.class, 1L, trainingType);
        }

        @Test
        @DisplayName("Should preserve training type name on save")
        void saveShouldPreserveTypeName() {
            doNothing().when(storage).put(eq(TrainingType.class), anyLong(), any());

            TrainingType result = trainingTypeDao.save(trainingType);

            assertEquals("Cardio", result.getTrainingTypeName());
        }
    }

    @Nested
    @DisplayName("FindById operations")
    class FindByIdTests {

        @Test
        @DisplayName("Should return training type when found")
        void findByIdShouldReturnTrainingTypeWhenExists() {
            when(storage.<TrainingType>get(TrainingType.class, 1L)).thenReturn(Optional.of(trainingType));

            TrainingType result = trainingTypeDao.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getTrainingTypeId());
            assertEquals("Cardio", result.getTrainingTypeName());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when training type not found")
        void findByIdShouldThrowExceptionWhenNotExists() {
            when(storage.<TrainingType>get(TrainingType.class, 999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> trainingTypeDao.findById(999L)
            );

            assertTrue(exception.getMessage().contains("TrainingType"));
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("FindByCondition operations")
    class FindByConditionTests {

        @Test
        @DisplayName("Should return training types matching condition")
        void findByConditionShouldReturnMatchingTrainingTypes() {
            TrainingType strengthType = TrainingType.builder()
                    .trainingTypeId(2L)
                    .trainingTypeName("Strength")
                    .build();

            TrainingType yogaType = TrainingType.builder()
                    .trainingTypeId(3L)
                    .trainingTypeName("Yoga")
                    .build();

            when(storage.<TrainingType>getByCondition(eq(TrainingType.class), any()))
                    .thenReturn(List.of(trainingType, strengthType, yogaType));

            List<TrainingType> result = trainingTypeDao.findByCondition(t -> true);

            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(t -> t.getTrainingTypeName().equals("Cardio")));
            assertTrue(result.stream().anyMatch(t -> t.getTrainingTypeName().equals("Strength")));
            assertTrue(result.stream().anyMatch(t -> t.getTrainingTypeName().equals("Yoga")));
        }

        @Test
        @DisplayName("Should return empty list when no training types match")
        void findByConditionShouldReturnEmptyListWhenNoMatch() {
            when(storage.<TrainingType>getByCondition(eq(TrainingType.class), any()))
                    .thenReturn(List.of());

            List<TrainingType> result = trainingTypeDao.findByCondition(t -> false);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Delete operations")
    class DeleteTests {

        @Test
        @DisplayName("Should delete training type by id")
        void deleteByIdShouldCallStorageDelete() {
            doNothing().when(storage).delete(TrainingType.class, 1L);

            trainingTypeDao.deleteById(1L);

            verify(storage, times(1)).delete(TrainingType.class, 1L);
        }
    }
}
