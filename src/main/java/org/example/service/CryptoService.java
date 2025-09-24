package org.example.service;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.SecureRandom;


public class CryptoService {


    private static final int SALT_LENGTH = 32;

    private final SecureRandom secureRandom;

    public CryptoService() {
        this.secureRandom = new SecureRandom();
    }

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }
}
