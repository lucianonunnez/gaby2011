package utils;  // Ajusta el paquete según tu estructura

import java.util.regex.Pattern;

public class ValidationUtils {
    // Patrones regex (copiados de UsuarioServiceImpl)
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^\\d+$"); // Solo números
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$");

    // Método para validar documento
    public static void validateDocumento(String documento) throws IllegalArgumentException {
        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio.");
        }
        if (!DOCUMENTO_PATTERN.matcher(documento).matches()) {
            throw new IllegalArgumentException("El documento debe contener solo números.");
        }
        if (documento.length() != 7) {
            throw new IllegalArgumentException("El documento debe tener exactamente 7 dígitos.");
        }
    }

    // Método para validar contraseña
    public static void validatePassword(String password) throws IllegalArgumentException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 7 caracteres, incluir una mayúscula, un número y un símbolo (ej. @, $, !, %, *, ?, &).");
        }
        if (password.length() > 16) {
            throw new IllegalArgumentException("La contraseña no puede exceder los 16 caracteres.");
        }
    }
}