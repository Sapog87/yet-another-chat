package org.sber.yetanotherchat.exception;

public class IvalidPeerException extends RuntimeException {
    public IvalidPeerException() {
    }

    public IvalidPeerException(String message) {
        super(message);
    }

    public IvalidPeerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IvalidPeerException(Throwable cause) {
        super(cause);
    }
}
