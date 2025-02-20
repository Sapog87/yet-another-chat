package ru.sber.yetanotherchat.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;
import ru.sber.yetanotherchat.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

import static ru.sber.yetanotherchat.exception.ErrorStatuses.*;

/**
 *
 */
@Slf4j
@RestControllerAdvice(
        basePackageClasses = {
                GroupController.class,
                MessageController.class,
                UserController.class
        })
public class RestErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerError> handleException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ServerError> handleException(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ServerError.builder()
                        .error(NOT_FOUND.getText())
                        .code(NOT_FOUND.getCode())
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