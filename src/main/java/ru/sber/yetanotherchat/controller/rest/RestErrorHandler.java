package ru.sber.yetanotherchat.controller.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static ru.sber.yetanotherchat.exception.ErrorStatuses.BAD_REQUEST;
import static ru.sber.yetanotherchat.exception.ErrorStatuses.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice(
        basePackageClasses = {
                GroupController.class,
                MessageController.class,
                UserController.class
        })
public class RestErrorHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ServerError> handleException(ConstraintViolationException e) {
        String violation = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining());
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(violation)
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ServerError> handleException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .internalServerError()
                .body(ServerError.builder()
                        .error(INTERNAL_SERVER_ERROR.getText())
                        .code(INTERNAL_SERVER_ERROR.getCode())
                        .message("Internal Server Error")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(InvalidPeerException.class)
    public ResponseEntity<ServerError> handleException(InvalidPeerException e) {
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ServerError> handleException(AccessDeniedException e) {
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }
}