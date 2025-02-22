package ru.sber.yetanotherchat.exception;

/**
 * Исключение, которое выбрасывается, когда чат не найден.
 */
public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException() {
    }

    public ChatNotFoundException(String message) {
        super(message);
    }

    public ChatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatNotFoundException(Throwable cause) {
        super(cause);
    }
}
