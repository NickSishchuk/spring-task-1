package com.example.gym.service;

import com.example.gym.dao.TrainingDao;
import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.model.Training;
import com.example.gym.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

	@Mock
	private TrainingDao trainingDao;

	private TrainingService trainingService;

	private Training training;

	@BeforeEach
	void setUp() {
		trainingService = new TrainingService(trainingDao);

		TrainingType cardioType = TrainingType.builder()
				.trainingTypeId(1L)
				.trainingTypeName("Cardio")
				.build();

		training = Training.builder()
				.trainingId(1L)
				.traineeId(1L)
				.trainerId(1L)
				.trainingName("Morning Cardio")
				.trainingType(cardioType)
				.trainingDate("2024-01-15")
				.trainingDuration(45)
				.build();
	}

	@Test
	@DisplayName("Create should save and return training")
	void createShouldSaveAndReturnTraining() {
		when(trainingDao.save(any(Training.class))).thenReturn(training);

		Training result = trainingService.create(training);

		assertNotNull(result);
		assertEquals("Morning Cardio", result.getTrainingName());
		assertEquals("Cardio", result.getTrainingType().getTrainingTypeName());
		verify(trainingDao, times(1)).save(training);
	}

	@Test
	@DisplayName("Select training returns training when found")
	void selectShouldReturnTrainingWhenFound() {
		when(trainingDao.findById(1L)).thenReturn(training);

		Training result = trainingService.select(1L);

		assertNotNull(result);
		assertEquals(1L, result.getTrainingId());
		assertEquals("Morning Cardio", result.getTrainingName());
	}

	@Test
	@DisplayName("Select training throws exception when not found")
	void selectShouldThrowExceptionWhenNotFound() {
		when(trainingDao.findById(999L)).thenThrow(new EntityNotFoundException("Training", 999L));

		EntityNotFoundException exception = assertThrows(
				EntityNotFoundException.class,
				() -> trainingService.select(999L)
		);
		assertTrue(exception.getMessage().contains("999"));
	}

	@Test
	@DisplayName("Delete training calls DAO delete method")
	void deleteShouldCallDaoDelete() {
		doNothing().when(trainingDao).deleteById(1L);

		trainingService.delete(1L);

		verify(trainingDao, times(1)).deleteById(1L);
	}
}