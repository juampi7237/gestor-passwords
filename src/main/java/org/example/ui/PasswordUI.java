package org.example.ui;

import org.example.model.PasswordEntity;
import org.example.service.AuthenticationService;
import org.example.service.PasswordGeneratorService;
import org.example.service.PasswordStorageService;

import java.util.Scanner;

public class PasswordUI {

    private final Scanner scanner;
    private final AuthenticationService authService;
    private final PasswordStorageService storageService;
    private final PasswordGeneratorService passwordGenerator;

    private boolean isAuthenticated = false;

    public PasswordUI(Scanner scanner, AuthenticationService authService,
                      PasswordStorageService storageService, PasswordGeneratorService passwordGenerator) {
        this.scanner = scanner;
        this.authService = authService;
        this.storageService = storageService;
        this.passwordGenerator = passwordGenerator;
    }

    public void showWelcomeMessage() {
        System.out.println("===========================================");
        System.out.println("    GESTOR DE PASSWORDS    ");
        System.out.println("===========================================");
        System.out.println();
    }

    public boolean authenticateUser() {
        System.out.println("AUTENTICACION REQUERIDA");
        System.out.println("Debe configurar o ingresar el password maestro");
        System.out.println();

        if (authService.hasMasterPassword()) {
            System.out.println("🔑 INICIAR SESION");
            String password = getInput("Ingrese su password maestro: ");

            try {
                if (authService.verifyMasterPassword(password)) {
                    System.out.println("Autenticacion exitosa");
                    isAuthenticated = true;
                    return true;
                } else {
                    System.out.println("Password maestro incorrecto");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Error en la autenticacion: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("ESTADO INICIAL");
            return false;
        }
    }

    public void savePassword() {
        System.out.println("\n💾 GUARDAR NUEVO PASSWORD");
        String site = getInputWithValidation("Sitio/Servicio: ", InputValidator.Validators.notEmpty());
        String username = getInputWithValidation("Usuario: ", InputValidator.Validators.notEmpty());

        System.out.print("¿Generar password automaticamente? (s/N): ");
        String generateOption = scanner.nextLine();

        String password;
        if (generateOption.equalsIgnoreCase("s")) {
            password = passwordGenerator.generateSecurePassword(12);
            System.out.println("🔐 Password generado: " + password);
        } else {
            password = getInputWithValidation("Password: ", InputValidator.Validators.notEmpty());
        }

        String notes = getInput("Notas (opcional): ");

        try {
            PasswordEntity entry = new PasswordEntity(site, username, password, notes);
            storageService.savePassword(entry);
            System.out.println("Password guardado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al guardar la password: " + e.getMessage());
        }
    }

    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    private String getInputWithValidation(String prompt, InputValidator validator) {
        while (true) {
            String input = getInput(prompt);
            String error = validator.validate(input);
            if (error == null) {
                return input;
            }
            System.out.println("❌ " + error);
        }
    }
}
