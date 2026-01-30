package com.example.gym.service;

import com.example.gym.dao.TrainerDao;
import com.example.gym.model.Trainer;
import com.example.gym.utils.UserCredentialsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrainerService extends AbstractService<Trainer, Long, TrainerDao> {

    private UserCredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.dao = trainerDao;
    }

    @Autowired
    public void setCredentialsGenerator(UserCredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainer create(Trainer trainer) {
        trainer.setUsername(credentialsGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                dao::existsByUsername
        ));
        trainer.setPassword(credentialsGenerator.generatePassword());
        log.info("Creating trainer with username: {}", trainer.getUsername());
        return dao.save(trainer);
    }

    public Trainer update(Long id, Trainer updatedTrainer) {
        Trainer existing = dao.findById(id);

        existing.setFirstName(updatedTrainer.getFirstName());
        existing.setLastName(updatedTrainer.getLastName());
        existing.setSpecialization(updatedTrainer.getSpecialization());
        existing.setActive(updatedTrainer.isActive());

        log.info("Updating trainer with id: {}", id);
        return dao.save(existing);
    }
}
