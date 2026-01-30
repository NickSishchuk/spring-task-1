package com.example.gym.service;

import com.example.gym.dao.TraineeDao;
import com.example.gym.exception.EntityNotFoundException;
import com.example.gym.model.Trainee;
import com.example.gym.utils.UserCredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserCredentialsGenerator credentialsGenerator;

    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService(traineeDao, credentialsGenerator);

        trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth("1990-01-01");
        trainee.setAddress("123 Main St");
        trainee.setActive(true);
    }

    @Test
    @DisplayName("Create trainee generates username in correct format")
    void createShouldGenerateUsername() {
        when(credentialsGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                .thenReturn("John.Doe");
        when(credentialsGenerator.generatePassword()).thenReturn("ab123cdefG");
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = traineeService.create(trainee);

        assertEquals("John.Doe", result.getUsername());
        verify(traineeDao).save(trainee);
    }

    @Test
    @DisplayName("Create trainee generates password with 10 characters")
    void createShouldGeneratePasswordWith10Characters() {
        when(credentialsGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("John.Doe");
        when(credentialsGenerator.generatePassword())
                .thenReturn("ab123cdefG");
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = traineeService.create(trainee);

        assertNotNull(result.getPassword());
        assertEquals(10, result.getPassword().length());
    }

    @Test
    @DisplayName("Select trainee returns trainee when found")
    void selectShouldReturnTraineeWhenFound() {
        when(traineeDao.findById(1L)).thenReturn(trainee);

        Trainee result = traineeService.select(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    @DisplayName("Select trainee throws exception when not found")
    void selectShouldThrowExceptionWhenNotFound() {
        when(traineeDao.findById(999L)).thenThrow(new EntityNotFoundException("Trainee", 999L));

        assertThrows(EntityNotFoundException.class, () -> traineeService.select(999L));
    }

    @Test
    @DisplayName("Update trainee updates fields correctly")
    void updateShouldUpdateFields() {
        Trainee existingTrainee = new Trainee();
        existingTrainee.setUserId(1L);
        existingTrainee.setFirstName("John");
        existingTrainee.setLastName("Doe");
        existingTrainee.setUsername("John.Doe");
        existingTrainee.setPassword("abc123xyz0");

        Trainee updatedData = new Trainee();
        updatedData.setFirstName("Johnny");
        updatedData.setLastName("Smith");
        updatedData.setDateOfBirth("1991-02-02");
        updatedData.setAddress("456 New St");
        updatedData.setActive(false);

        when(traineeDao.findById(1L)).thenReturn(existingTrainee);
        when(traineeDao.save(any(Trainee.class))).thenReturn(existingTrainee);

        Trainee result = traineeService.update(1L, updatedData);

        assertEquals("Johnny", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("456 New St", result.getAddress());
        assertFalse(result.isActive());
        assertEquals("John.Doe", result.getUsername());
        assertEquals("abc123xyz0", result.getPassword());
    }

    @Test
    @DisplayName("Delete trainee calls DAO delete method")
    void deleteShouldCallDaoDelete() {
        doNothing().when(traineeDao).deleteById(1L);

        traineeService.delete(1L);

        verify(traineeDao, times(1)).deleteById(1L);
    }
}
