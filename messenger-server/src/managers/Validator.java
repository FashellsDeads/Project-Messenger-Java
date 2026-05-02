package managers;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public static void validateRegistration(String username, String email, String password) throws ValidationException {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("Имя пользователя должно быть от 3 до 20 символов и содержать только буквы, цифры или подчеркивание");
        }

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Некорректный формат Email");
        }

        if (password == null || password.isEmpty()) {
            throw new ValidationException("Пароль не может быть пустым");
        }
        if (password.length() < 6) {
            throw new ValidationException("Пароль должен быть не менее 6 символов");
        }
    }

    public static void validateLogin(String email, String password) throws ValidationException {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Пароль не может быть пустым");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
