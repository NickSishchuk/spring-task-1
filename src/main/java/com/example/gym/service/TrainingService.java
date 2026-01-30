package com.example.gym.service;

import com.example.gym.dao.TrainingDao;
import com.example.gym.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrainingService extends AbstractService<Training, Long, TrainingDao> {

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.dao = trainingDao;
    }

    public Training create(Training training) {
        log.info("Creating training: {}", training.getTrainingName());
        return dao.save(training);
    }
}
