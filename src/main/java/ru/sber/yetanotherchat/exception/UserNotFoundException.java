package ru.sber.yetanotherchat.exception;

/**
 * Исключение, которое выбрасывается, когда пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
