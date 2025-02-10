package org.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.entity.Chat;
import org.sber.yetanotherchat.entity.Message;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.entity.UserMessage;
import org.sber.yetanotherchat.repository.MessageRepository;
import org.sber.yetanotherchat.repository.UserMessageRepository;
import org.sber.yetanotherchat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserMessageRepository userMessageRepository;

    @Transactional
    public Message createMessage(User sender, Chat chat, String text, Long randomId) {
        Message message = messageRepository.save(getMessage(sender, chat, text, randomId));
        userMessageRepository.saveAll(getUserMessages(message));
        return message;
    }

    private Message getMessage(User sender, Chat chat, String text, Long randomId) {
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(text);
        message.setRandomId(randomId);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    private List<UserMessage> getUserMessages(Message message) {
        Chat chat = message.getChat();
        List<User> members = userRepository.findAllByChats(chat);
        return members.stream().map(member -> {
            UserMessage userMessage = new UserMessage();
            userMessage.setMessage(message);
            userMessage.setChat(chat);
            userMessage.setReceiver(member);
            return userMessage;
        }).toList();
    }
}