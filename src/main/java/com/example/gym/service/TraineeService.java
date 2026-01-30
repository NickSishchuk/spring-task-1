package com.example.gym.service;

import com.example.gym.dao.TraineeDao;
import com.example.gym.model.Trainee;
import com.example.gym.utils.UserCredentialsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TraineeService extends AbstractService<Trainee, Long, TraineeDao> {

    private UserCredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.dao = traineeDao;
    }

    @Autowired
    public void setCredentialsGenerator(UserCredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainee create(Trainee trainee) {
        trainee.setUsername(credentialsGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                dao::existsByUsername
        ));
        trainee.setPassword(credentialsGenerator.generatePassword());
        log.info("Creating trainee with username: {}", trainee.getUsername());
        return dao.save(trainee);
    }

    public Trainee update(Long id, Trainee updatedTrainee) {
        Trainee existing = dao.findById(id);

        existing.setFirstName(updatedTrainee.getFirstName());
        existing.setLastName(updatedTrainee.getLastName());
        existing.setDateOfBirth(updatedTrainee.getDateOfBirth());
        existing.setAddress(updatedTrainee.getAddress());
        existing.setActive(updatedTrainee.isActive());

        log.info("Updating trainee with id: {}", id);
        return dao.save(existing);
    }
}
