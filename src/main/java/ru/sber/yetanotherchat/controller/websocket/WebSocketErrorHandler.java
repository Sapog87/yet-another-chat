package ru.sber.yetanotherchat.controller.websocket;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@ControllerAdvice(basePackageClasses = WebSocketMessageController.class)
public class WebSocketErrorHandler {
    //TODO
    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public Map<String, String> handleException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of(
                "error", "Internal Server Error",
                "type", "INTERNAL_SERVER_ERROR",
                "code", "500",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public Map<String, String> handleException(InvalidPeerException e) {
        return Map.of(
                "error", "Bad Request",
                "type", "PEER_ID_INVALID",
                "code", "400",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public Map<String, String> handleException(AccessDeniedException e) {
        return Map.of(
                "error", "Bad Request",
                "type", "ACCESS_DENIED",
                "code", "400",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public Map<String, String> handleException(ConstraintViolationException e) {
        return Map.of(
                "error", "Bad Request",
                "type", "VALIDATION_ERROR",
                "code", "400",
                "timestamp", LocalDateTime.now().toString()
        );
    }
}