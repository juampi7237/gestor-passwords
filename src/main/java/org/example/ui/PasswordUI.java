package org.example.ui;

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

    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
