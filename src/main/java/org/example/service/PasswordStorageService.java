package org.example.service;

import org.example.model.PasswordEntity;
import org.example.model.StorageEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PasswordStorageService {

    private final CryptoService cryptoService;
    private final Path passwordsFile;
    private final Path storageDirectory;

    private List<PasswordEntity> passwordList;
    private String currentMasterPassword;

    public PasswordStorageService(CryptoService cryptoService, Path storageDirectory, Path passwordsFile) {
        this.cryptoService = cryptoService;
        this.storageDirectory = storageDirectory;
        this.passwordsFile = passwordsFile;
        this.passwordList = new ArrayList<>();
    }

    public void savePassword(PasswordEntity password) throws Exception {
        validateAuthentication();

        Optional<PasswordEntity> existing = passwordList.stream()
                .filter(p -> p.getSite().equalsIgnoreCase(password.getSite()) &&
                        p.getUsername().equalsIgnoreCase(password.getUsername()))
                .findFirst();

        if (existing.isPresent()) {
            passwordList.remove(existing.get());
        }

        passwordList.add(password);
        savePasswordsToFile();
    }

    public List<PasswordEntity> getAllPasswords() throws Exception {
        validateAuthentication();
        return new ArrayList<>(passwordList);
    }

    public List<PasswordEntity> searchPasswords(String searchTerm) throws Exception {
        validateAuthentication();

        String lowerSearchTerm = searchTerm.toLowerCase();
        return passwordList.stream()
                .filter(entry -> entry.getSite().toLowerCase().contains(lowerSearchTerm) ||
                        entry.getUsername().toLowerCase().contains(lowerSearchTerm) ||
                        entry.getNotes().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    public boolean deletePassword(String site, String username) throws Exception {
        validateAuthentication();

        boolean removed = passwordList.removeIf(entry ->
                entry.getSite().equalsIgnoreCase(site) &&
                        entry.getUsername().equalsIgnoreCase(username));

        if (removed) {
            savePasswordsToFile();
        }

        return removed;
    }


    private void savePasswordsToFile() throws Exception {
        validateAuthentication();

        String passwordsData = passwordList.stream()
                .map(PasswordEntity::toCsvString)
                .collect(Collectors.joining("\n"));

        String encryptedData = cryptoService.encrypt(passwordsData, currentMasterPassword);
        Files.write(passwordsFile, encryptedData.getBytes());
    }

    private List<PasswordEntity> parsePasswordsFromString(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(data.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .map(line -> {
                    try {
                        return PasswordEntity.fromCsvString(line);
                    } catch (Exception e) {
                        System.err.println("Error parsing password: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateAuthentication() throws Exception {
        if (currentMasterPassword == null) {
            throw new IllegalStateException("Debe autenticarse primero");
        }
    }
}
