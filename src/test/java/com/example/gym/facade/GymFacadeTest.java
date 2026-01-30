package com.example.gym.facade;

import com.example.gym.model.Trainee;
import com.example.gym.model.Trainer;
import com.example.gym.model.Training;
import com.example.gym.service.TraineeService;
import com.example.gym.service.TrainerService;
import com.example.gym.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade gymFacade;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);

        trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        trainer = new Trainer();
        trainer.setTrainerId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        training = Training.builder()
                .trainingId(1L)
                .trainingName("Morning Cardio")
                .build();
    }

    @Test
    @DisplayName("CreateTrainee should delegate to TraineeService")
    void createTraineeShouldDelegateToService() {
        when(traineeService.create(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee(trainee);

        assertEquals(trainee, result);
        verify(traineeService, times(1)).create(trainee);
    }

    @Test
    @DisplayName("SelectTrainee should delegate to TraineeService")
    void selectTraineeShouldDelegateToService() {
        when(traineeService.select(1L)).thenReturn(trainee);

        Trainee result = gymFacade.selectTrainee(1L);

        assertEquals(trainee, result);
        verify(traineeService, times(1)).select(1L);
    }

    @Test
    @DisplayName("UpdateTrainee should delegate to TraineeService")
    void updateTraineeShouldDelegateToService() {
        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setFirstName("Johnny");

        when(traineeService.update(1L, updatedTrainee)).thenReturn(updatedTrainee);

        Trainee result = gymFacade.updateTrainee(1L, updatedTrainee);

        assertEquals("Johnny", result.getFirstName());
        verify(traineeService, times(1)).update(1L, updatedTrainee);
    }

    @Test
    @DisplayName("DeleteTrainee should delegate to TraineeService")
    void deleteTraineeShouldDelegateToService() {
        doNothing().when(traineeService).delete(1L);

        gymFacade.deleteTrainee(1L);

        verify(traineeService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("CreateTrainer should delegate to TrainerService")
    void createTrainerShouldDelegateToService() {
        when(trainerService.create(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer(trainer);

        assertEquals(trainer, result);
        verify(trainerService, times(1)).create(trainer);
    }

    @Test
    @DisplayName("SelectTrainer should delegate to TrainerService")
    void selectTrainerShouldDelegateToService() {
        when(trainerService.select(1L)).thenReturn(trainer);

        Trainer result = gymFacade.selectTrainer(1L);

        assertEquals(trainer, result);
        verify(trainerService, times(1)).select(1L);
    }

    @Test
    @DisplayName("UpdateTrainer should delegate to TrainerService")
    void updateTrainerShouldDelegateToService() {
        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setFirstName("Janet");

        when(trainerService.update(1L, updatedTrainer)).thenReturn(updatedTrainer);

        Trainer result = gymFacade.updateTrainer(1L, updatedTrainer);

        assertEquals("Janet", result.getFirstName());
        verify(trainerService, times(1)).update(1L, updatedTrainer);
    }

    @Test
    @DisplayName("CreateTraining should delegate to TrainingService")
    void createTrainingShouldDelegateToService() {
        when(trainingService.create(training)).thenReturn(training);

        Training result = gymFacade.createTraining(training);

        assertEquals(training, result);
        verify(trainingService, times(1)).create(training);
    }

    @Test
    @DisplayName("SelectTraining should delegate to TrainingService")
    void selectTrainingShouldDelegateToService() {
        when(trainingService.select(1L)).thenReturn(training);

        Training result = gymFacade.selectTraining(1L);

        assertEquals(training, result);
        verify(trainingService, times(1)).select(1L);
    }

    @Test
    @DisplayName("Facade should use constructor injection")
    void facadeShouldUseConstructorInjection() {
        assertNotNull(gymFacade);

        when(traineeService.select(1L)).thenReturn(trainee);
        when(trainerService.select(1L)).thenReturn(trainer);
        when(trainingService.select(1L)).thenReturn(training);

        assertNotNull(gymFacade.selectTrainee(1L));
        assertNotNull(gymFacade.selectTrainer(1L));
        assertNotNull(gymFacade.selectTraining(1L));
    }
}
