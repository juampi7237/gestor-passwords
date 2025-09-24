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


    public void changeMasterPassword(String currentPassword, String newPassword) throws Exception {
        if (!verifyMasterPassword(currentPassword)) {
            throw new SecurityException("Contraseña maestra actual incorrecta");
        }

        passwordStorageService.reencryptPasswords(currentPassword, newPassword);
        setMasterPassword(newPassword);
    }

    public boolean verifyMasterPassword(String masterPassword) throws Exception {
        if (!hasMasterPassword()) return false;

        String storedHash = Files.readString(masterHashFile);
        boolean isValid = cryptoService.verifyPasswordHash(masterPassword, storedHash);

        if (isValid) {
            this.currentMasterPassword = masterPassword;
            passwordStorageService.loadPasswords(masterPassword);
        }

        return isValid;
    }

    public boolean hasMasterPassword() {
        return Files.exists(masterHashFile);
    }
}
