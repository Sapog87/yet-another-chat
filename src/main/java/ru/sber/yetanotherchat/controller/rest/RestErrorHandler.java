package ru.sber.yetanotherchat.controller.rest;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sber.yetanotherchat.dto.ServerError;

@RestControllerAdvice(
        basePackageClasses = {
                GroupController.class,
                MessageController.class,
                PeerController.class
        })
public class RestErrorHandler {
    //TODO

    @ExceptionHandler(ConstraintViolationException.class)
    public ServerError handleException(ConstraintViolationException e) {
        return null;
    }
}