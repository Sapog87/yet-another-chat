package ru.sber.yetanotherchat.exception;

/**
 * Исключение, которое выбрасывается, когда пользователь с таким идентификатором или именем уже существует.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
