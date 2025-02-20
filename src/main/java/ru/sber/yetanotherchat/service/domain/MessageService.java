package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.repository.MessageRepository;

import java.time.LocalDateTime;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    /**
     * @param sender
     * @param chat
     * @param text
     * @return
     */
    @Transactional
    public Message createMessage(User sender, Chat chat, String text) {
        return messageRepository.save(getMessage(sender, chat, text));
    }

    private Message getMessage(User sender, Chat chat, String text) {
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(text);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}