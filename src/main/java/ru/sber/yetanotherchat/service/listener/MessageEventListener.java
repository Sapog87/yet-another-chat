package ru.sber.yetanotherchat.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.sber.yetanotherchat.dto.MessageReceivedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener
    public void sendMessages(MessageReceivedEvent event) {
        event.getRecipients()
                .forEach(username ->
                        messagingTemplate.convertAndSendToUser(
                                username,
                                "/topic/update",
                                event.getMessage()
                        )
                );
    }
}