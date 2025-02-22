package ru.sber.yetanotherchat.exception;

/**
 * Исключение, которое выбрасывается, когда указанный peer является недействительным.
 */
public class InvalidPeerException extends RuntimeException {
    public InvalidPeerException() {
    }

    public InvalidPeerException(String message) {
        super(message);
    }

    public InvalidPeerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPeerException(Throwable cause) {
        super(cause);
    }
}
