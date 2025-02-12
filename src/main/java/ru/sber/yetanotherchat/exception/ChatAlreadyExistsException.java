package ru.sber.yetanotherchat.exception;

public class ChatAlreadyExistsException extends RuntimeException {
    public ChatAlreadyExistsException() {
    }

    public ChatAlreadyExistsException(String message) {
        super(message);
    }

    public ChatAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
