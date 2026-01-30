package com.example.gym.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;

@Component
public class UserCredentialsGenerator {

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    public String generateUsername(String firstName, String lastName,
                                   Predicate<String> usernameExists) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 0;

        while (usernameExists.test(username)) {
            counter++;
            username = baseUsername + counter;
        }
        return username;
    }

    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
