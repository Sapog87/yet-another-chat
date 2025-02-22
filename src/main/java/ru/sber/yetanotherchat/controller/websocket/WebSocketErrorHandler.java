package ru.sber.yetanotherchat.controller.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.exception.InvalidPeerException;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.util.ServerErrorUtil;

import java.time.LocalDateTime;

import static ru.sber.yetanotherchat.exception.ErrorMessages.*;
import static ru.sber.yetanotherchat.exception.ErrorStatuses.*;

/**
 * Обработчик исключений из websocket контроллера
 */
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
                .message(INTERNAL_ERROR)
                .build();
    }

    @MessageExceptionHandler(InvalidPeerException.class)
    public ServerError handleException(InvalidPeerException e) {
        return ServerError.builder()
                .error(BAD_REQUEST.getText())
                .code(BAD_REQUEST.getCode())
                .timestamp(LocalDateTime.now())
                .message(INVALID_PEER)
                .build();
    }

    @MessageExceptionHandler(UnreachablePeerException.class)
    public ServerError handleException(UnreachablePeerException e) {
        return ServerError.builder()
                .error(BAD_REQUEST.getText())
                .code(BAD_REQUEST.getCode())
                .timestamp(LocalDateTime.now())
                .message(UNREACHABLE_PEER)
                .build();
    }

    @MessageExceptionHandler(PeerNotFoundException.class)
    public ServerError handleException(PeerNotFoundException e) {
        return ServerError.builder()
                .error(NOT_FOUND.getText())
                .code(NOT_FOUND.getCode())
                .timestamp(LocalDateTime.now())
                .message(PEER_NOT_FOUND)
                .build();
    }

    @MessageExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ServerError> handleException(HandlerMethodValidationException e) {
        var errors = ServerErrorUtil.getStringStringHashMap(e);

        return ResponseEntity
                .badRequest()
                .body(ServerError.builder()
                        .error(BAD_REQUEST.getText())
                        .code(BAD_REQUEST.getCode())
                        .timestamp(LocalDateTime.now())
                        .message(errors.toString())
                        .build());
    }


}