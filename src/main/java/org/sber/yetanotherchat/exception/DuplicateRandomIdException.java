package org.sber.yetanotherchat.exception;

public class DuplicateRandomIdException extends RuntimeException {
    public DuplicateRandomIdException() {
    }

    public DuplicateRandomIdException(String message) {
        super(message);
    }

    public DuplicateRandomIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRandomIdException(Throwable cause) {
        super(cause);
    }
}
