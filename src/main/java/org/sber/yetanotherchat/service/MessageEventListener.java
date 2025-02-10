package org.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sber.yetanotherchat.dto.message.MessageReceivedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener
    public void sendMessages(MessageReceivedEvent event) {
        event.getRecipients().forEach(username -> {
            messagingTemplate.convertAndSendToUser(username, "/chat/update", event.getMessage());
        });
    }
}