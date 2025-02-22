package ru.sber.yetanotherchat.exception;

/**
 * Исключение, которое выбрасывается, когда указанный peer недоступен.
 */
public class UnreachablePeerException extends RuntimeException {
    public UnreachablePeerException() {
    }

    public UnreachablePeerException(String message) {
        super(message);
    }

    public UnreachablePeerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnreachablePeerException(Throwable cause) {
        super(cause);
    }
}
