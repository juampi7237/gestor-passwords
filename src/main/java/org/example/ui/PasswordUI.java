package org.example.ui;

import org.example.model.PasswordEntity;
import org.example.service.AuthenticationService;
import org.example.service.PasswordGeneratorService;
import org.example.service.PasswordStorageService;

import java.util.List;
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
        System.out.println("AUTENTICACIÓN REQUERIDA");
        System.out.println("Para acceder al gestor, debe configurar o ingresar su password maestro.");
        System.out.println();

        if (authService.hasMasterPassword()) {
            return handleLogin();
        } else {
            return handleInitialSetup();
        }
    }

    private boolean handleLogin() {
        System.out.println("INICIAR SESIÓN");
        String password = getInput("Ingrese su password maestro: ");

        try {
            if (authService.verifyMasterPassword(password)) {
                System.out.println("Autenticacion exitosa.");
                isAuthenticated = true;
                return true;
            } else {
                System.out.println("Password maestro incorrecta.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error durante la autenticacion: " + e.getMessage());
            return false;
        }
    }

    private boolean handleInitialSetup() {
        System.out.println("CONFIGURACION INICIAL");
        System.out.println("No se ha configurado una password maestro");

        String password = getInputWithValidation(
                "Ingrese un nuevo password maestro (mínimo 8 caracteres): ",
                InputValidator.Validators.minLength(8)
        );

        String confirmPassword = getInput("Confirme la password maestro: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Los passwords no coinciden");
            return false;
        }

        try {
            authService.setMasterPassword(password);
            System.out.println("Password maestro configurado exitosamente");
            isAuthenticated = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error al configurar el password maestro: " + e.getMessage());
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
            System.out.println("Password generado: " + password);
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

    public void listarPasswords() {
        System.out.println("\nLISTA DE PASSWORDS");
        try {
            List<PasswordEntity> passwords = storageService.getAllPasswords();
            if (passwords.isEmpty()) {
                System.out.println("No hay passwords guardados.");
                return;
            }

            for (int i = 0; i < passwords.size(); i++) {
                PasswordEntity password = passwords.get(i);
                System.out.printf("%d. %s\n", i + 1, password.getSite());
                System.out.printf(" Usuario: %s\n", password.getUsername());
                System.out.printf(" Password: %s\n", password.getPassword());
                if (!password.getNotes().isEmpty()) {
                    System.out.printf(" Notas: %s\n", password.getNotes());
                }
                System.out.printf(" Creado: %s\n", password.getFormattedCreatedAt());
                System.out.println("-------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error al listar passwords: " + e.getMessage());
        }
    }

    public void buscarPassword() {
        System.out.println("\nBUSCAR PASSWORD");
        String searchTerm = getInput("Ingrese el sitio/servicio a buscar: ");

        try {
            List<PasswordEntity> passwords = storageService.searchPasswords(searchTerm);
            if (passwords.isEmpty()) {
                System.out.println("🔍 No se encontraron passwords para: " + searchTerm);
                return;
            }

            System.out.println("===========================================");
            System.out.println("RESULTADOS DE BÚSQUEDA:");
            System.out.println("===========================================");
            for (int i = 0; i < passwords.size(); i++) {
                PasswordEntity password = passwords.get(i);
                System.out.printf("%d. %s\n", i + 1, password.getSite());
                System.out.printf(" Usuario: %s\n", password.getUsername());
                System.out.printf(" Password: %s\n", password.getPassword());
                if (!password.getNotes().isEmpty()) {
                    System.out.printf(" Notas: %s\n", password.getNotes());
                }
                System.out.printf(" Creado: %s\n", password.getFormattedCreatedAt());
                System.out.println("-------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar passwords: " + e.getMessage());
        }
    }

    public void deletePassword() {
        System.out.println("\n🗑️ ELIMINAR CONTRASEÑA");

        try {
            List<PasswordEntity> passwords = storageService.getAllPasswords();
            if (passwords.isEmpty()) {
                System.out.println("No hay passwords para eliminar.");
                return;
            }

            System.out.println("Seleccione el password a eliminar:");
            for (int i = 0; i < passwords.size(); i++) {
                PasswordEntity entry = passwords.get(i);
                System.out.printf("%d. %s (%s)\n", i + 1, entry.getSite(), entry.getUsername());
            }

            String input = getInput("Numero de password a eliminar: ");

            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < passwords.size()) {
                    PasswordEntity entry = passwords.get(index);
                    System.out.printf("¿Esta seguro de eliminar el password de %s? (s/N): ", entry.getSite());
                    String confirm = scanner.nextLine();

                    if (confirm.equalsIgnoreCase("s") || confirm.equalsIgnoreCase("si")) {
                        storageService.deletePassword(entry.getSite(), entry.getUsername());
                        System.out.println("Password eliminado exitosamente");
                    } else {
                        System.out.println("Eliminacion cancelada");
                    }
                } else {
                    System.out.println("Numero no valido");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no valido. Debe ser un numero");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar password: " + e.getMessage());
        }
    }

    public void updateMasterPassword() {
        System.out.println("\nCAMBIAR PASSWORD MAESTRO");
        String currentPassword = getInput("Ingrese la password maestro actual: ");

        try {
            if (!authService.verifyMasterPassword(currentPassword)) {
                System.out.println("Password maestro actual incorrecto");
                return;
            }

            String newPassword = getInputWithValidation(
                    "Ingrese la nuevo password maestro: ",
                    InputValidator.Validators.minLength(8)
            );

            String confirmPassword = getInput("Confirme nuevo password maestro: ");

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Las passwords no coinciden.");
                return;
            }

            authService.changeMasterPassword(currentPassword, newPassword);
            System.out.println("Password maestro cambiado exitosamente");

        } catch (Exception e) {
            System.out.println("Error al cambiar password maestro: " + e.getMessage());
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
