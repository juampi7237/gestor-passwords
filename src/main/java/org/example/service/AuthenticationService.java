package org.example.service;

import java.nio.file.Files;
import java.nio.file.Path;

public class AuthenticationService {

    private final PasswordStorageService passwordStorageService;
    private final CryptoService cryptoService;
    private final Path masterHashFile;


    private String currentMasterPassword;

    public AuthenticationService(PasswordStorageService passwordStorageService, CryptoService cryptoService, Path masterHashFile) {
        this.passwordStorageService = passwordStorageService;
        this.cryptoService = cryptoService;
        this.masterHashFile = masterHashFile;
    }

    public void setMasterPassword(String masterPassword) throws Exception {
        if (masterPassword.length() < 8) {
            throw new IllegalArgumentException("El password maestro debe tener al menos 8 caracteres");
        }

        byte[] salt = cryptoService.generateSalt();
        String hashedPassword = cryptoService.hashPasswordWithSalt(masterPassword, salt);
        Files.write(masterHashFile, hashedPassword.getBytes());
        this.currentMasterPassword = masterPassword;
    }
}
