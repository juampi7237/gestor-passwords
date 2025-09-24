package org.example.ui;

@FunctionalInterface
public interface InputValidator {
    String validate(String input);

    // Clase interna con validadores predefinidos
    class Validators {
        public static InputValidator notEmpty() {
            return input -> input == null || input.trim().isEmpty() ?
                    "Este campo no puede estar vacío" : null;
        }

        public static InputValidator minLength(int min) {
            return input -> input.length() < min ?
                    String.format("Debe tener al menos %d caracteres", min) : null;
        }

        public static InputValidator passwordStrength() {
            return input -> {
                if (input.length() < 8) return "El password debe tener al menos 8 caracteres";
                if (!input.matches(".*[A-Z].*")) return "Debe contener al menos una mayuscula";
                if (!input.matches(".*[a-z].*")) return "Debe contener al menos una minuscula";
                if (!input.matches(".*\\d.*")) return "Debe contener al menos un numero";
                return null;
            };
        }
    }
}

