package org.example.service;

import java.security.SecureRandom;

public class PasswordGeneratorService {

    private final SecureRandom secureRandom;

    public PasswordGeneratorService() {
        this.secureRandom = new SecureRandom();
    }

    public String generateSecurePassword(int length) {
        if (length < 4) length = 12;

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upperCase + lowerCase + numbers + specialChars;

        StringBuilder password = new StringBuilder();
        password.append(upperCase.charAt(secureRandom.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(secureRandom.nextInt(lowerCase.length())));
        password.append(numbers.charAt(secureRandom.nextInt(numbers.length())));
        password.append(specialChars.charAt(secureRandom.nextInt(specialChars.length())));

        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }

        return shuffleString(password.toString());
    }

    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
