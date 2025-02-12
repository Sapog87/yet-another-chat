package ru.sber.yetanotherchat.exception;

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
