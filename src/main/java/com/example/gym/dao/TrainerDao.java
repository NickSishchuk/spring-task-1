package com.example.gym.dao;

import com.example.gym.model.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TrainerDao extends AbstractDao<Trainer, Long> {

    public TrainerDao() {
        super(Trainer.class);
    }

    @Override
    protected Long getId(Trainer entity) {
        return entity.getUserId();
    }

    @Override
    protected void setId(Trainer entity, Long id) {
        entity.setUserId(id);
    }
}
