package ru.sber.yetanotherchat.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.InvalidPeerException;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.util.ServerErrorUtil;

import java.time.LocalDateTime;

import static ru.sber.yetanotherchat.exception.ErrorMessages.*;
import static ru.sber.yetanotherchat.exception.ErrorStatuses.*;

/**
 * Обработчик исключений для rest контроллеров.
 */
@Slf4j
@RestControllerAdvice(
        basePackageClasses = {
                GroupController.class,
                MessageController.class,
                UserController.class
        })
public class RestErrorHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ServerError> handleException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .internalServerError()
                .body(ServerError.builder()
                        .error(INTERNAL_SERVER_ERROR.getText())
                        .code(INTERNAL_SERVER_ERROR.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(INTERNAL_ERROR)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ServerError> handleException(MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage(), e);
        var errors = ServerErrorUtil.getErrors(e);

        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(errors.toString())
                        .build());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ServerError> handleException(HandlerMethodValidationException e) {
        log.warn(e.getMessage(), e);
        var errors = ServerErrorUtil.getErrors(e);

        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(errors.toString())
                        .build());
    }

    @ExceptionHandler(InvalidPeerException.class)
    public ResponseEntity<ServerError> handleException(InvalidPeerException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(INVALID_PEER)
                        .build());
    }

    @ExceptionHandler(UnreachablePeerException.class)
    public ResponseEntity<ServerError> handleException(UnreachablePeerException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(UNREACHABLE_PEER)
                        .build());
    }

    @ExceptionHandler(PeerNotFoundException.class)
    public ResponseEntity<ServerError> handleException(PeerNotFoundException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ServerError.builder()
                        .error(NOT_FOUND.getText())
                        .code(NOT_FOUND.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(PEER_NOT_FOUND)
                        .build());
    }
}
