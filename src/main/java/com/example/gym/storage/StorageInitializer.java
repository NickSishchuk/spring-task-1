package com.example.gym.storage;

import com.example.gym.model.Trainee;
import com.example.gym.model.Trainer;
import com.example.gym.model.Training;
import com.example.gym.model.TrainingType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class StorageInitializer implements BeanPostProcessor {

    @Value("${storage.init.file:}")
    private String initFilePath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Function<Map<String, Object>, EntityConversionResult>> converters = new HashMap<>();

    public StorageInitializer() {
        converters.put("trainer", this::convertToTrainer);
        converters.put("trainee", this::convertToTrainee);
        converters.put("training", this::convertToTraining);
        converters.put("trainingtype", this::convertToTrainingType);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof InMemoryStorage storage) {
            if (initFilePath != null && !initFilePath.isEmpty()) {
                loadDataIntoStorage(storage);
            }
            log.info("Storage initialized with {} entries", storage.size());
        }
        return bean;
    }

    private void loadDataIntoStorage(InMemoryStorage storage) {
        try {
            String resourcePath = initFilePath.replace("classpath:", "");
            ClassPathResource resource = new ClassPathResource(resourcePath);
            InputStream inputStream = resource.getInputStream();

            Map<String, Map<String, Object>> data = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );

            for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                String key = entry.getKey();
                Map<String, Object> value = entry.getValue();

                EntityConversionResult result = convertToEntity(key, value);
                if (result != null) {
                    storage.put(result.entityClass(), result.id(), result.entity());
                    storage.initializeIdCounter(result.entityClass(), result.id());
                    log.debug("Loaded entity with key: {}", key);
                }
            }

            log.info("Successfully loaded {} entities from {}", data.size(), initFilePath);

        } catch (IOException e) {
            log.error("Failed to load initial data from file: {}", initFilePath, e);
        }
    }

    private EntityConversionResult convertToEntity(String key, Map<String, Object> data) {
        String namespace = key.split(":")[0].toLowerCase();
        Long id = Long.parseLong(key.split(":")[1]);

        Function<Map<String, Object>, EntityConversionResult> converter = converters.get(namespace);
        if (converter == null) {
            log.warn("Unknown entity type for key: {}", key);
            return null;
        }

        try {
            return converter.apply(data);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert entity with key: {}", key, e);
            return null;
        }
    }

    private EntityConversionResult convertToTrainer(Map<String, Object> data) {
        Trainer trainer = objectMapper.convertValue(data, Trainer.class);
        return new EntityConversionResult(Trainer.class, trainer.getUserId(), trainer);
    }

    private EntityConversionResult convertToTrainee(Map<String, Object> data) {
        Trainee trainee = objectMapper.convertValue(data, Trainee.class);
        return new EntityConversionResult(Trainee.class, trainee.getUserId(), trainee);
    }

    private EntityConversionResult convertToTraining(Map<String, Object> data) {
        Training training = objectMapper.convertValue(data, Training.class);
        return new EntityConversionResult(Training.class, training.getTrainingId(), training);
    }

    private EntityConversionResult convertToTrainingType(Map<String, Object> data) {
        TrainingType trainingType = objectMapper.convertValue(data, TrainingType.class);
        return new EntityConversionResult(TrainingType.class, trainingType.getTrainingTypeId(), trainingType);
    }

    private record EntityConversionResult(Class<?> entityClass, Long id, Object entity) {}
}
