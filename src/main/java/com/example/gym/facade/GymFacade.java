package com.example.gym.facade;

import com.example.gym.model.Trainee;
import com.example.gym.model.Trainer;
import com.example.gym.model.Training;
import com.example.gym.service.TraineeService;
import com.example.gym.service.TrainerService;
import com.example.gym.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        log.info("GymFacade initialized");
    }

    public Trainee createTrainee(Trainee trainee) {
        log.info("Facade: Creating trainee");
        return traineeService.create(trainee);
    }

    public Trainee selectTrainee(Long id) {
        log.info("Facade: Selecting trainee with id: {}", id);
        return traineeService.select(id);
    }

    public Trainee updateTrainee(Long id, Trainee trainee) {
        log.info("Facade: Updating trainee with id: {}", id);
        return traineeService.update(id, trainee);
    }

    public void deleteTrainee(Long id) {
        log.info("Facade: Deleting trainee with id: {}", id);
        traineeService.delete(id);
    }

    public Trainer createTrainer(Trainer trainer) {
        log.info("Facade: Creating trainer");
        return trainerService.create(trainer);
    }

    public Trainer selectTrainer(Long id) {
        log.info("Facade: Selecting trainer with id: {}", id);
        return trainerService.select(id);
    }

    public Trainer updateTrainer(Long id, Trainer trainer) {
        log.info("Facade: Updating trainer with id: {}", id);
        return trainerService.update(id, trainer);
    }

    public Training createTraining(Training training) {
        log.info("Facade: Creating training");
        return trainingService.create(training);
    }

    public Training selectTraining(Long id) {
        log.info("Facade: Selecting training with id: {}", id);
        return trainingService.select(id);
    }
}
