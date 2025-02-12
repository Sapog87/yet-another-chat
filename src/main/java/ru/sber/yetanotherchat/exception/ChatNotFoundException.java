package ru.sber.yetanotherchat.exception;

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
