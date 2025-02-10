package org.sber.yetanotherchat.controller;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.message.StatusOutputMessage;
import org.sber.yetanotherchat.service.WebSocketUserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketUserController {
    private final WebSocketUserService userService;

    @MessageMapping("/user/status")
    @SendToUser("/user/status")
    public StatusOutputMessage status(Principal principal) {
        return null;
    }
}
