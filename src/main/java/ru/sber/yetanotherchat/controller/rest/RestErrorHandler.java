package ru.sber.yetanotherchat.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.InvalidPeerException;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

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
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ServerError> handleException(HandlerMethodValidationException e) {
        var errors = new HashMap<String, String>();

        e.getValueResults().forEach(
                result -> result.getResolvableErrors()
                        .forEach(error -> {
                            String param = (error instanceof ObjectError objectError ?
                                    objectError.getObjectName() :
                                    ((MessageSourceResolvable) Objects.requireNonNull(error.getArguments())[0])
                                            .getDefaultMessage());

                            param = (result.getContainerIndex() != null ?
                                    param + "[" + result.getContainerIndex() + "]" : param);

                            errors.put(param, error.getDefaultMessage());
                        })
        );

        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(errors.toString())
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

    @ExceptionHandler(UnreachablePeerException.class)
    public ResponseEntity<ServerError> handleException(UnreachablePeerException e) {
        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(PeerNotFoundException.class)
    public ResponseEntity<ServerError> handleException(PeerNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ServerError.builder()
                        .error(NOT_FOUND.getText())
                        .code(NOT_FOUND.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }
}