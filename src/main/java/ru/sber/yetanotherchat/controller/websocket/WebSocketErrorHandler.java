package ru.sber.yetanotherchat.controller.websocket;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;

@Slf4j
@ControllerAdvice(basePackageClasses = WebSocketMessageController.class)
@SendToUser("/topic/error")
public class WebSocketErrorHandler {
    //TODO
    @MessageExceptionHandler
    public ServerError handleException(RuntimeException e) {
        log.error(e.getMessage(), e);
//        return Map.of(
//                "error", "Internal Server Error",
//                "type", "INTERNAL_SERVER_ERROR",
//                "code", "500",
//                "timestamp", LocalDateTime.now().toString()
//        );
        return null;
    }

    @MessageExceptionHandler
    public ServerError handleException(InvalidPeerException e) {
//        return Map.of(
//                "error", "Bad Request",
//                "type", "PEER_ID_INVALID",
//                "code", "400",
//                "timestamp", LocalDateTime.now().toString()
//        );
        return null;
    }

    @MessageExceptionHandler
    public ServerError handleException(AccessDeniedException e) {
//        return Map.of(
//                "error", "Bad Request",
//                "type", "ACCESS_DENIED",
//                "code", "400",
//                "timestamp", LocalDateTime.now().toString()
//
//        );
        return null;
    }

    @MessageExceptionHandler
    public ServerError handleException(ConstraintViolationException e) {
//        return Map.of(
//                "error", "Bad Request",
//                "type", "VALIDATION_ERROR",
//                "code", "400",
//                "timestamp", LocalDateTime.now().toString()
//        );
        return null;
    }
}