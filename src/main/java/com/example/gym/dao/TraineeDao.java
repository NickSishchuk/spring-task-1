package com.example.gym.dao;

import com.example.gym.model.Trainee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TraineeDao extends AbstractDao<Trainee, Long> {

    public TraineeDao() {
        super(Trainee.class);
    }

    @Override
    protected Long getId(Trainee entity) {
        return entity.getUserId();
    }

    @Override
    protected void setId(Trainee entity, Long id) {
        entity.setUserId(id);
    }
}
