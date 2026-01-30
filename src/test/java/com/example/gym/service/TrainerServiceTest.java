package com.example.gym.service;

import com.example.gym.dao.TrainerDao;
import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.model.Trainer;
import com.example.gym.utils.UserCredentialsGenerator;
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
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserCredentialsGenerator credentialsGenerator;

    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService();

        trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setSpecialization("Cardio");
        trainer.setActive(true);
    }

    @Test
    @DisplayName("Create trainer generates username in correct format")
    void createShouldGenerateUsername() {
        when(credentialsGenerator.generateUsername(eq("Jane"), eq("Smith"), any()))
                .thenReturn("Jane.Smith");
        when(credentialsGenerator.generatePassword())
                .thenReturn("zxcvbnmqwe");
        when(trainerDao.save(any(Trainer.class))).thenReturn(trainer);

        Trainer result = trainerService.create(trainer);

        assertEquals("Jane.Smith", result.getUsername());
        verify(trainerDao).save(trainer);
    }

    @Test
    @DisplayName("Select trainer returns trainer when found")
    void selectShouldReturnTrainerWhenFound() {
        when(trainerDao.findById(1L)).thenReturn(trainer);

        Trainer result = trainerService.select(1L);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Cardio", result.getSpecialization());
    }

    @Test
    @DisplayName("Select trainer throws exception if not found")
    void selectShouldThrowExceptionIfNotFound() {
        when(trainerDao.findById(999L)).thenThrow(new EntityNotFoundException("Trainer", 999L));

        assertThrows(EntityNotFoundException.class, () -> trainerService.select(999L));
    }

    @Test
    @DisplayName("Update trainer updates fields correctly")
    void updateShouldUpdateFields() {
        Trainer existingTrainer = new Trainer();
        existingTrainer.setUserId(1L);
        existingTrainer.setFirstName("Jane");
        existingTrainer.setLastName("Smith");
        existingTrainer.setUsername("Jane.Smith");
        existingTrainer.setPassword("abc123xyz0");
        existingTrainer.setSpecialization("Cardio");

        Trainer updatedData = new Trainer();
        updatedData.setFirstName("Janet");
        updatedData.setLastName("Johnson");
        updatedData.setSpecialization("Yoga");
        updatedData.setActive(false);

        when(trainerDao.findById(1L)).thenReturn(existingTrainer);
        when(trainerDao.save(any(Trainer.class))).thenReturn(existingTrainer);

        Trainer result = trainerService.update(1L, updatedData);

        assertEquals("Janet", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        assertEquals("Yoga", result.getSpecialization());
        assertFalse(result.isActive());
        assertEquals("Jane.Smith", result.getUsername());
        assertEquals("abc123xyz0", result.getPassword());
    }

    @Test
    @DisplayName("Delete trainer calls DAO delete method")
    void deleteShouldCallDaoDelete() {
        doNothing().when(trainerDao).deleteById(1L);

        trainerService.delete(1L);

        verify(trainerDao, times(1)).deleteById(1L);
    }
}
