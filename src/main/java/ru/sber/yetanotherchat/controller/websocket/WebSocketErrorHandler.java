package ru.sber.yetanotherchat.controller.websocket;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static ru.sber.yetanotherchat.exception.ErrorStatuses.BAD_REQUEST;
import static ru.sber.yetanotherchat.exception.ErrorStatuses.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice(basePackageClasses = WebSocketMessageController.class)
@SendToUser("/topic/error")
public class WebSocketErrorHandler {
    @MessageExceptionHandler(RuntimeException.class)
    public ServerError handleException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ServerError.builder()
                .error(INTERNAL_SERVER_ERROR.getText())
                .code(INTERNAL_SERVER_ERROR.getCode())
                .timestamp(LocalDateTime.now())
                .message("Internal Server Error")
                .build();
    }

    @MessageExceptionHandler(InvalidPeerException.class)
    public ServerError handleException(InvalidPeerException e) {
        return ServerError.builder()
                .error(BAD_REQUEST.getText())
                .code(BAD_REQUEST.getCode())
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .build();
    }

    @MessageExceptionHandler(AccessDeniedException.class)
    public ServerError handleException(AccessDeniedException e) {
        return ServerError.builder()
                .error(BAD_REQUEST.getText())
                .code(BAD_REQUEST.getCode())
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .build();
    }

    @MessageExceptionHandler(ConstraintViolationException.class)
    public ServerError handleException(ConstraintViolationException e) {
        String violation = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining());
        return ServerError.builder()
                .error(BAD_REQUEST.getText())
                .code(BAD_REQUEST.getCode())
                .timestamp(LocalDateTime.now())
                .message(violation)
                .build();
    }
}