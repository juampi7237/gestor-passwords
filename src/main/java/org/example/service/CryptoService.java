package org.example.service;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;


public class CryptoService {


    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_SHA256 = "PBKDF2WithHmacSHA256";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int SALT_LENGTH = 32;
    private static final int KEY_LENGTH = 256;
    private static final int PBKDF2_ITERATIONS = 100000;

    private final SecureRandom secureRandom;

    public CryptoService() {
        this.secureRandom = new SecureRandom();
    }

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public SecretKey deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM_SHA256);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        spec.clearPassword();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String plaintext, String password) throws Exception {
        byte[] salt = generateSalt();
        SecretKey key = deriveKeyFromPassword(password, salt);

        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] result = new byte[salt.length + iv.length + ciphertext.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(iv, 0, result, salt.length, iv.length);
        System.arraycopy(ciphertext, 0, result, salt.length + iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public String decrypt(String encryptedData, String password) throws Exception {
        byte[] data = Base64.getDecoder().decode(encryptedData);

        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(data, 0, salt, 0, SALT_LENGTH);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(data, SALT_LENGTH, iv, 0, GCM_IV_LENGTH);

        byte[] ciphertext = new byte[data.length - SALT_LENGTH - GCM_IV_LENGTH];
        System.arraycopy(data, SALT_LENGTH + GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

        SecretKey key = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    public String hashPasswordWithSalt(String password, byte[] salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        byte[] result = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(hash, 0, result, salt.length, hash.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public boolean verifyPasswordHash(String password, String hashedPassword) throws Exception {
        byte[] data = Base64.getDecoder().decode(hashedPassword);
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(data, 0, salt, 0, SALT_LENGTH);

        String newHash = hashPasswordWithSalt(password, salt);
        return constantTimeEquals(hashedPassword, newHash);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
