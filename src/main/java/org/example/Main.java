package org.example;
import org.example.service.*;
import org.example.ui.MenuUI;
import org.example.ui.PasswordUI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final String STORAGE_DIR = "secure_passwords";
    private static final String PASSWORDS_FILE = "passwords.enc";
    private static final String MASTER_HASH_FILE = "master.hash";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Initialize services
            CryptoService cryptoService = new CryptoService();
            PasswordGeneratorService passwordGenerator = new PasswordGeneratorService();

            Path storageDir = getStorageDirectory();
            Path passwordsFile = storageDir.resolve(PASSWORDS_FILE);
            Path masterHashFile = storageDir.resolve(MASTER_HASH_FILE);

            PasswordStorageService storageService = new PasswordStorageService(
                    cryptoService, storageDir, passwordsFile);
            AuthenticationService authService = new AuthenticationService(
                    storageService, cryptoService, masterHashFile);

            PasswordUI ui = new PasswordUI(
                    scanner, authService, storageService, passwordGenerator);
            MenuUI menuManager = new MenuUI(ui);

            ui.showWelcomeMessage();

            if (ui.authenticateUser()) {
                menuManager.showMainMenu();
            } else {
                System.out.println("Autenticación fallida. Saliendo...");
            }

        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static Path getStorageDirectory() {
        String currentDir = System.getProperty("user.dir");
        Path projectRoot = Paths.get(currentDir);


        if (projectRoot.endsWith("classes") &&
                projectRoot.getParent().getFileName().toString().equals("build")) {
            return projectRoot.getParent().getParent().resolve(STORAGE_DIR);
        }

        if (projectRoot.endsWith("build") || projectRoot.endsWith("src")) {
            return projectRoot.getParent().resolve(STORAGE_DIR);
        }

        return projectRoot.resolve(STORAGE_DIR);
    }
}