package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.MessageReceivedEvent;
import ru.sber.yetanotherchat.dto.SendMessageDto;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.*;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.MessageService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

/**
 * Сервис для работы с сообщениями.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {
    private final ApplicationEventPublisher eventPublisher;
    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;

    /**
     * Отправка сообщения.
     * <p>
     * Обрабатывает отправку сообщения пользователем.
     * Сообщение будет отправлено в соответствующий чат.
     *
     * @param dto    объект, содержащий данные для отправки сообщения
     * @param sender пользователь, отправляющий сообщение
     * @return {@link MessageDto}
     */
    @Transactional
    public MessageDto sendMessage(SendMessageDto dto, Principal sender) {
        var user = userService.findUserByUsername(sender.getName());

        var peerId = dto.getPeerId();
        var chat = getChatForSend(peerId, user);
        var message = messageService.createMessage(user, chat, dto.getText());

        var members = userService.findChatMembers(chat);
        var reversePeerId = peerId < 0 ? peerId : user.getId();
        //уведомляет слушающий сервис о том, что пришло сообщение и его нужно отправить получателям
        publishMessageReceivedEvent(message, members, reversePeerId, user);

        return getMessageDto(peerId, message, true);
    }

    /**
     * Получение истории сообщений.
     * <p>
     * Этот метод используется для получения истории сообщений между двумя пользователями.
     *
     * @param dto    объект, содержащий данные для получения сообщения
     * @param sender пользователь, запрашивающий историю
     * @return {@link List<MessageDto>}
     */
    @Transactional(readOnly = true)
    public List<MessageDto> fetchHistory(FetchHistoryDto dto, Principal sender) {
        var user = userService.findUserByUsername(sender.getName());
        var limit = dto.getLimit();
        var peerId = dto.getPeerId();
        var offsetId = dto.getOffsetId();
        try {
            var chat = getChatForRead(peerId, user);
            var messages = messageService.fetchMessagesFromChat(chat, limit, offsetId);

            return messages.stream()
                    .map(message -> {
                        var outgoing = message.getSender().equals(user);
                        return getMessageDto(peerId, message, outgoing);
                    })
                    .toList();
        } catch (PersonalChatNotExitsException e) {
            log.warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    private Chat getChatForSend(Long peerId, User user) {
        if (peerId == 0) {
            throw new InvalidPeerException("peerId не может быть 0");
        }

        if (peerId > 0) {
            var recipient = getUser(peerId);
            return chatService.findOrCreatePersonalChat(user, recipient);
        }

        return getGroup(Math.abs(peerId), user);
    }

    private Chat getChatForRead(Long peerId, User user) {
        if (peerId == 0) {
            throw new InvalidPeerException("peerId не может быть 0");
        }

        if (peerId > 0) {
            var recipient = getUser(peerId);
            try {
                return chatService.findPersonalChat(user, recipient);
            } catch (ChatNotFoundException e) {
                throw new PersonalChatNotExitsException(
                        "Личного чата между пользователями {%d} и {%d} не существует"
                                .formatted(user.getId(), recipient.getId()), e);
            }
        }

        return getGroup(Math.abs(peerId), user);
    }

    private User getUser(Long id) {
        try {
            return userService.findUserById(id);
        } catch (UserNotFoundException e) {
            throw new PeerNotFoundException(
                    "Пользователь с id {%d} не найден"
                            .formatted(id), e);
        }
    }

    private Chat getGroup(Long id, User user) {
        var chat = getChat(id);
        if (Boolean.FALSE.equals(chat.getIsGroup())) {
            throw new PeerNotFoundException(
                    "Группа с таким id {%d} не найдена"
                            .formatted(id));
        }
        if (!chatService.isMemberOfChat(user, chat)) {
            throw new UnreachablePeerException(
                    "Пользователь {%d} не состоит в группе {%d}"
                            .formatted(user.getId(), id));
        }
        return chat;
    }

    private Chat getChat(Long id) {
        try {
            return chatService.findChatById(id);
        } catch (ChatNotFoundException e) {
            throw new PeerNotFoundException(
                    "Группа с таким id {%d} не найдена"
                            .formatted(id), e);
        }
    }

    private void publishMessageReceivedEvent(Message message, List<User> members, Long peerId, User sender) {
        var recipients = members.stream()
                .filter(recipient -> !sender.equals(recipient))
                .map(User::getUsername)
                .toList();

        var messageDto = getMessageDto(peerId, message, false);

        eventPublisher.publishEvent(
                MessageReceivedEvent.builder()
                        .message(messageDto)
                        .recipients(recipients)
                        .build());
    }

    private MessageDto getMessageDto(Long peerId, Message message, Boolean outgoing) {
        return MessageDto.builder()
                .id(message.getId())
                .peerId(peerId)
                .text(message.getText())
                .createdAt(message.getCreatedAt())
                .senderName(message.getSender().getName())
                .outgoing(outgoing)
                .build();
    }
}
