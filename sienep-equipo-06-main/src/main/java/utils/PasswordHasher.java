package utils;

public class PasswordHasher {
    public static String hash(String password) {
        StringBuilder sb = new StringBuilder();
        for (char c : password.toCharArray()) {
            sb.append((char)(c + 3)); // desplazamiento de 3 posiciones
        }
        return sb.reverse().toString();
    }

    public static boolean verify(String password, String hashed) {
        return hash(password).equals(hashed);
    }
}