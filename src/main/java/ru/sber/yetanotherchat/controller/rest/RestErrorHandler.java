package ru.sber.yetanotherchat.controller.rest;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {
        GroupController.class,
        MessageController.class,
        PeerController.class})
public class RestErrorHandler {
    //TODO
}
