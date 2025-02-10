package org.sber.yetanotherchat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.message.ChatInputMessage;
import org.sber.yetanotherchat.dto.message.ChatOutputMessage;
import org.sber.yetanotherchat.dto.message.HistoryInputMessage;
import org.sber.yetanotherchat.dto.message.HistoryOutputMessage;
import org.sber.yetanotherchat.service.WebSocketMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {
    private final WebSocketMessageService websocketMessageService;

    @MessageMapping("/chat/message")
    @SendToUser("/chat/message")
    public ChatOutputMessage message(@Payload @Valid ChatInputMessage chatInputMessage, Principal principal) {
        return websocketMessageService.sendMessage(chatInputMessage, principal);
    }

    @MessageMapping("/chat/history")
    @SendToUser("/chat/history")
    public HistoryOutputMessage history(@Payload @Valid HistoryInputMessage historyInputMessage, Principal principal) {
        HistoryOutputMessage history = websocketMessageService.getHistory(historyInputMessage, principal);
        if (history.getMessages().isEmpty()) {
            return null;
        }
        return history;
    }
}