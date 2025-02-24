package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с {@link Message}.
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    private static final Limit DEFAULT_LIMIT = Limit.of(20);
    private final MessageRepository messageRepository;

    /**
     * Создает новое сообщение в указанном чате от указанного пользователя.
     *
     * @param sender отправитель сообщения
     * @param chat   чат, в который отправляется сообщение
     * @param text   текст сообщения
     * @return {@link Message} - созданное сообщение
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

    /**
     * Извлекает сообщения из чата с поддержкой пагинации. Если передан offsetId,
     * сообщения извлекаются до этого идентификатора.
     *
     * @param chat     чат, из которого извлекаются сообщения
     * @param limit    максимальное количество сообщений для извлечения
     * @param offsetId идентификатор последнего сообщения, до которого нужно извлечь
     * @return {@link List<Message>} - список сообщений, удовлетворяющих условиям
     */
    public List<Message> fetchMessagesFromChat(Chat chat, Integer limit, Long offsetId) {
        var jpaLimit = limit == null || limit < 1 ? DEFAULT_LIMIT : Limit.of(limit);

        if (offsetId != null && offsetId > 0) {
            return messageRepository.findMessagesByChatAndIdLessThanOrderByIdDesc(chat, offsetId, jpaLimit);
        }
        return messageRepository.findMessagesByChatOrderByIdDesc(chat, jpaLimit);
    }
}
