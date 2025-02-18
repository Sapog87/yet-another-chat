package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.MessageReceivedEvent;
import ru.sber.yetanotherchat.dto.SendMessageDto;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.repository.MessageRepository;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.MessageService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

import static ru.sber.yetanotherchat.exception.ErrorMessages.INVALID_PEER;
import static ru.sber.yetanotherchat.exception.ErrorMessages.PEER_ACCESS_DENIED;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {
    private static final Limit DEFAULT_LIMIT = Limit.of(20);
    private final ApplicationEventPublisher eventPublisher;
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;

    /**
     * @param sendMessageDto
     * @param sender
     * @return
     */
    @Override
    @Transactional
    public MessageDto sendMessage(SendMessageDto sendMessageDto, Principal sender) {
        var peerId = sendMessageDto.getPeerId();
        var user = userService.findUserByUsername(sender.getName());

        var chat = getOrCreateChat(peerId, user);

        var message = messageService.createMessage(user, chat, sendMessageDto.getText());

        var members = userService.findChatMembers(chat);
        var reversePeerId = peerId < 0 ? peerId : user.getId();
        publishMessageReceivedEvent(message, members, reversePeerId, user);

        return getMessageDto(peerId, message, true);
    }

    private Chat getOrCreateChat(Long peerId, User user) {
        try {
            // если peerId > 0 => попытка отправить сообщение пользователю с id = peerId
            // если peerId < 0 => попытка отправить сообщение в групповой чат с id = -peerId
            if (peerId > 0) {
                var recipient = userService.findUserById(peerId);
                return chatService.findOrCreatePersonalChat(user, recipient);
            } else if (peerId < 0) {
                var chatId = Math.abs(peerId);
                return getGroup(chatId, user);
            }
            throw new InvalidPeerException(INVALID_PEER);
        } catch (UserNotFoundException | ChatNotFoundException e) {
            throw new InvalidPeerException(INVALID_PEER, e);
        }
    }

    /**
     * @param fetchHistoryDto
     * @param sender
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> fetchHistory(FetchHistoryDto fetchHistoryDto, Principal sender) {
        var peerId = fetchHistoryDto.getPeerId();
        var user = userService.findUserByUsername(sender.getName());

        var chat = getChatToFetchHistory(peerId, user);

        var messages = getMessages(fetchHistoryDto, chat);
        return messages.stream()
                .map(message ->
                        message.getSender().equals(user) ?
                                getMessageDto(peerId, message, true) :
                                getMessageDto(peerId, message, false)
                ).toList();
    }

    private Chat getChatToFetchHistory(Long peerId, User user) {
        try {
            // если peerId > 0 => попытка получить историю сообщений с пользователем с id = peerId
            // если peerId < 0 => попытка получить историю сообщений из группового чат с id = -peerId
            if (peerId > 0) {
                var recipient = userService.findUserById(peerId);
                return chatService.findPersonalChat(user, recipient);
            } else if (peerId < 0) {
                var chatId = Math.abs(peerId);
                return getGroup(chatId, user);
            }
            throw new InvalidPeerException(INVALID_PEER);
        } catch (UserNotFoundException | ChatNotFoundException e) {
            throw new InvalidPeerException(INVALID_PEER, e);
        }
    }

    private Chat getGroup(Long chatId, User user) {
        var chat = chatService.findChatById(chatId);
        if (Boolean.FALSE.equals(chat.getIsGroup())) {
            throw new InvalidPeerException(INVALID_PEER);
        }
        if (!chatService.isMemberOfChat(user, chat)) {
            throw new AccessDeniedException(PEER_ACCESS_DENIED);
        }
        return chat;
    }

    private void publishMessageReceivedEvent(Message message, List<User> members, Long peerId, User sender) {
        var recipients = members.stream()
                .filter(recipient -> !sender.equals(recipient))
                .map(User::getUsername).toList();

        var messageDto = getMessageDto(peerId, message, false);

        eventPublisher.publishEvent(
                MessageReceivedEvent.builder()
                        .message(messageDto)
                        .recipients(recipients)
                        .build());
    }

    private List<Message> getMessages(FetchHistoryDto fetchHistoryDto, Chat chat) {
        var limit = fetchHistoryDto.getLimit();
        var offsetId = fetchHistoryDto.getOffsetId();
        var jpaLimit = limit == null || limit < 1 ? DEFAULT_LIMIT : Limit.of(limit);

        if (offsetId != null && offsetId > 0) {
            return messageRepository.findMessagesByChatAndIdLessThanOrderByIdDesc(chat, fetchHistoryDto.getOffsetId(), jpaLimit);
        }
        return messageRepository.findMessagesByChatOrderByIdDesc(chat, jpaLimit);
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