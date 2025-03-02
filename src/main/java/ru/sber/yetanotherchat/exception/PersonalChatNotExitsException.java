package ru.sber.yetanotherchat.exception;

/**
 * Исключение, которое выбрасывается, когда не существует личного чата между двумя пользователями.
 */
public class PersonalChatNotExitsException extends RuntimeException {
    public PersonalChatNotExitsException() {
    }

    public PersonalChatNotExitsException(String message) {
        super(message);
    }

    public PersonalChatNotExitsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonalChatNotExitsException(Throwable cause) {
        super(cause);
    }
}
