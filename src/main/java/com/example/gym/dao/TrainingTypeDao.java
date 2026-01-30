package com.example.gym.dao;

import com.example.gym.model.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TrainingTypeDao extends AbstractDao<TrainingType, Long> {

    public TrainingTypeDao() {
        super(TrainingType.class);
    }

    @Override
    protected Long getId(TrainingType entity) {
        return entity.getTrainingTypeId();
    }

    @Override
    protected void setId(TrainingType entity, Long id) {
        entity.setTrainingTypeId(id);
    }
}
