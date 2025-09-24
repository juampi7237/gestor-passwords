package org.example.ui;

public class MenuUI {

    private final PasswordUI ui;

    public MenuUI(PasswordUI ui) {
        this.ui = ui;
    }

    public void showMainMenu() {
        while (ui.isAuthenticated()) {
            System.out.println();
            System.out.println("===========================================");
            System.out.println("              MENU PRINCIPAL");
            System.out.println("===========================================");
            System.out.println("1. Guardar password");
            System.out.println("2. Listar todas los passwords");
            System.out.println("3. Buscar password");
            System.out.println("4. Eliminar password");
            System.out.println("5. Actualizar password maestro");
            System.out.println("6. Salir");
            System.out.println("===========================================");
            String option = ui.getInput("Seleccione una opcion: ");
            switch (option) {
                case "1":
                    ui.savePassword();
                    break;
                case "2":
                    System.out.println("Listar password");
                    break;
                case "3":
                    System.out.println("Buscar password");
                    break;
                case "4":
                    System.out.println("Eliminar password");
                    break;
                case "5":
                    System.out.println("Actualizar password");
                    break;
                case "6":
                    ui.setAuthenticated(false);
                    System.out.println("Adios cerrando programa");
                    break;
                default:
                    System.out.println("Opcion no valida. Intente nuevamente.");
            }
        }
    }

}
