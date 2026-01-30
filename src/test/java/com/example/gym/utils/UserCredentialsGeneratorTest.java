package com.example.gym.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialsGeneratorTest {

    private UserCredentialsGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new UserCredentialsGenerator();
    }

    @Test
    @DisplayName("Should generate username in FirstName.LastName format")
    void generateUsernameShouldUseCorrectFormat() {
        String result = generator.generateUsername("John", "Doe", username -> false);

        assertEquals("John.Doe", result);
    }

    @Test
    @DisplayName("Should add number on collision")
    void generateUsernameShouldAddSerialNumberOnCollision() {
        Predicate<String> usernameExists = username ->
                username.equals("John.Doe") || username.equals("John.Doe1");

        String result = generator.generateUsername("John", "Doe", usernameExists);

        assertEquals("John.Doe2", result);
    }

    @Test
    @DisplayName("Should increment serial number until unique")
    void generateUsernameShouldKeepIncrementingUntilUnique() {
        Set<String> existingUsernames = Set.of("Jane.Smith", "Jane.Smith1", "Jane.Smith2");

        String result = generator.generateUsername("Jane", "Smith",
                existingUsernames::contains);

        assertEquals("Jane.Smith3", result);
    }

    @Test
    @DisplayName("Should generate password with 10 characters")
    void generatePasswordShouldReturn10Characters() {
        String result = generator.generatePassword();

        assertEquals(10, result.length());
    }

    @Test
    @DisplayName("Should generate password with only alphanumeric characters")
    void generatePasswordShouldContainOnlyAlphanumeric() {
        String result = generator.generatePassword();

        assertTrue(result.matches("[A-Za-z0-9]+"));
    }

    @Test
    @DisplayName("Should generate unique passwords")
    void generatePasswordShouldBeUnique() {
        String password1 = generator.generatePassword();
        String password2 = generator.generatePassword();

        assertNotEquals(password1, password2);
    }
}
