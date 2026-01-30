package com.example.gym.dao;

import com.example.gym.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TrainingDao extends AbstractDao<Training, Long> {

    public TrainingDao() {
        super(Training.class);
    }

    @Override
    protected Long getId(Training entity) {
        return entity.getTrainingId();
    }

    @Override
    protected void setId(Training entity, Long id) {
        entity.setTrainingId(id);
    }
}
