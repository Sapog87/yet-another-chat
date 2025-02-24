package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
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
import java.util.List;

/**
 * Сервис для работы с сообщениями.
 */
@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {
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
     * @param sendMessageDto объект, содержащий данные для отправки сообщения
     * @param sender         пользователь, отправляющий сообщение
     * @return {@link MessageDto}
     */
    @Override
    @Transactional
    public MessageDto sendMessage(SendMessageDto sendMessageDto, Principal sender) {
        var user = userService.findUserByUsername(sender.getName());

        var peerId = sendMessageDto.getPeerId();
        var chat = getChat(peerId, user, false);
        var message = messageService.createMessage(user, chat, sendMessageDto.getText());

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
     * @param fetchHistoryDto объект, содержащий данные для получения сообщения
     * @param sender          пользователь, запрашивающий историю
     * @return {@link List<MessageDto>}
     */
    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> fetchHistory(FetchHistoryDto fetchHistoryDto, Principal sender) {
        var user = userService.findUserByUsername(sender.getName());

        var peerId = fetchHistoryDto.getPeerId();
        var limit = fetchHistoryDto.getLimit();
        var offsetId = fetchHistoryDto.getOffsetId();
        var chat = getChat(peerId, user, true);
        var messages = messageService.fetchMessagesFromChat(chat, limit, offsetId);

        return messages.stream()
                .map(message ->
                        message.getSender().equals(user)
                                ? getMessageDto(peerId, message, true)
                                : getMessageDto(peerId, message, false)
                ).toList();
    }


    private Chat getChat(Long peerId, User user, boolean readOnly) {
        try {
            // если peerId > 0 => пользователь с id = peerId
            // если peerId < 0 => группа с id = -peerId
            if (peerId > 0) {
                var recipient = userService.findUserById(peerId);
                if (readOnly) {
                    return chatService.findPersonalChat(user, recipient);
                }
                return chatService.findOrCreatePersonalChat(user, recipient);
            } else if (peerId < 0) {
                var chatId = Math.abs(peerId);
                return getGroup(chatId, user);
            }
            throw new InvalidPeerException("Peer id не может быть 0");
        } catch (UserNotFoundException | ChatNotFoundException e) {
            throw new PeerNotFoundException(
                    "Peer с таким id {%d} не найден"
                            .formatted(peerId), e);
        }
    }

    private Chat getGroup(Long id, User user) {
        var chat = chatService.findChatById(id);
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
