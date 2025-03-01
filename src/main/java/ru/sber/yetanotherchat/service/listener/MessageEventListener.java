package ru.sber.yetanotherchat.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.sber.yetanotherchat.dto.MessageReceivedEvent;

/**
 * Сервис для отправки сообщений получателям.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Метод для обработки события получения сообщения.
     * Этот метод отправляет сообщение всем получателям, указанным в событии.
     *
     * @param event событие, содержащее информацию о полученном сообщении и получателях
     */
    @TransactionalEventListener
    protected void sendMessages(MessageReceivedEvent event) {
        log.info("Start MessageEventListener::sendMessages with messageId: {}",
                event.getMessage().getId());

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
