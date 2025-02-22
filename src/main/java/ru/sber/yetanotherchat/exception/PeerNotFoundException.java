package ru.sber.yetanotherchat.exception;

/**
 *
 */
public class PeerNotFoundException extends RuntimeException {
    public PeerNotFoundException() {
    }

    public PeerNotFoundException(String message) {
        super(message);
    }

    public PeerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PeerNotFoundException(Throwable cause) {
        super(cause);
    }
}
