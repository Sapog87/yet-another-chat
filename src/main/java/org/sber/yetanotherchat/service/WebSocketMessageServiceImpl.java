package org.sber.yetanotherchat.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sber.yetanotherchat.dto.message.*;
import org.sber.yetanotherchat.entity.Chat;
import org.sber.yetanotherchat.entity.Message;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.exception.DuplicateRandomIdException;
import org.sber.yetanotherchat.exception.IvalidPeerException;
import org.sber.yetanotherchat.repository.ChatRepository;
import org.sber.yetanotherchat.repository.MessageRepository;
import org.sber.yetanotherchat.repository.UserRepository;
import org.sber.yetanotherchat.service.domain.ChatService;
import org.sber.yetanotherchat.service.domain.MessageService;
import org.sber.yetanotherchat.service.domain.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageServiceImpl implements WebSocketMessageService {
    private static final Limit DEFAULT_LIMIT = Limit.of(20);
    private final ApplicationEventPublisher eventPublisher;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;

    private Chat getOrCreateChatIfPeerIsUser(Long peerId, User sender) {
        User recipient = userService.findUserById(peerId);
        return chatRepository.findChatByUserAndPeerId(sender, peerId)
                .or(() -> {
                    if (peerId > 0) {
                        return Optional.of(chatService.createPersonalChat(sender, recipient));
                    }
                    return Optional.empty();
                })
                .orElseThrow(() -> new RuntimeException());
    }

    @Override
    @Transactional
    public ChatOutputMessage sendMessage(ChatInputMessage chatInputMessage, Principal sender) {
        Long peerId = chatInputMessage.getPeerId();
        validatePeerId(peerId);

        User user = userService.findUserByUsername(sender.getName());

        validateRandomId(chatInputMessage, user);

        Optional<Chat> optionalChat = chatRepository.findChatByUserAndPeerId(user, peerId);
        if (optionalChat.isEmpty()) {
//            if ()
        }

        Chat chat = getOrCreateChatIfPeerIsUser(peerId, user);

        Message message = messageService.createMessage(user, chat,
                chatInputMessage.getText(), chatInputMessage.getRandomId());
        List<User> members = userRepository.findAllByChats(chat);

        publishMessageReceivedEvent(message, members, chatInputMessage, user);

        return getChatOutputMessage(chatInputMessage, message, user);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryOutputMessage getHistory(HistoryInputMessage historyInputMessage, Principal sender) {
        validatePeerId(historyInputMessage.getPeerId());

        User user = userService.findUserByUsername(sender.getName());
        Optional<Chat> chat = chatRepository.findChatByUserAndPeerId(user, historyInputMessage.getPeerId());
        if (chat.isEmpty()) {
            return getOutputHistoryMessage(Collections.emptyList());
        }

        List<Message> messages = getMessages(historyInputMessage, chat.get());
        List<MessageInfo> messageInfos = getOutputMessages(historyInputMessage.getPeerId(), messages);
        return getOutputHistoryMessage(messageInfos);
    }

    private void validateRandomId(ChatInputMessage chatInputMessage, User sender) {
        if (messageRepository.existsByRandomIdAndSenderId(
                chatInputMessage.getRandomId(), sender.getId())) {
            throw new DuplicateRandomIdException("Random id already exists");
        }
    }

    private void validatePeerId(Long peerId) {
        if (peerId == 0) {
            throw new IvalidPeerException("Peer ID cannot be zero");
        }
    }

    private ChatOutputMessage getChatOutputMessage(ChatInputMessage chatInputMessage, Message message, User sender) {
        ChatOutputMessage chatOutputMessage = new ChatOutputMessage();
        chatOutputMessage.setId(message.getId());
        chatOutputMessage.setPeerId(chatInputMessage.getPeerId());
        chatOutputMessage.setText(message.getText());
        chatOutputMessage.setSenderName(sender.getName());
        chatOutputMessage.setCreatedAt(message.getCreatedAt());
        chatOutputMessage.setRandomId(chatInputMessage.getRandomId().toString());
        return chatOutputMessage;
    }

    private void publishMessageReceivedEvent(Message message, List<User> members, ChatInputMessage chatInputMessage, User sender) {
        List<String> recipients = members.stream()
                .filter(recipient -> !sender.equals(recipient))
                .map(User::getUsername)
                .toList();
        MessageInfo messageInfo = getMessageInfo(chatInputMessage.getPeerId(), message);
        MessageReceivedEvent event = getMessageReceivedEvent(recipients, messageInfo);
        eventPublisher.publishEvent(event);
    }

    private MessageReceivedEvent getMessageReceivedEvent(List<String> recipients, MessageInfo messageInfo) {
        MessageReceivedEvent event = new MessageReceivedEvent();
        event.setMessage(messageInfo);
        event.setRecipients(recipients);
        return event;
    }

    private MessageInfo getMessageInfo(Long peerId, Message message) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setPeerId(peerId);
        messageInfo.setId(message.getId());
        messageInfo.setText(message.getText());
        messageInfo.setCreatedAt(message.getCreatedAt());
        messageInfo.setSenderName(message.getSender().getName());
        return messageInfo;
    }

    private List<Message> getMessages(HistoryInputMessage historyInputMessage, Chat chat) {
        Integer limit = historyInputMessage.getLimit();
        Long offsetId = historyInputMessage.getOffsetId();
        Limit jpaLimit = limit == 0 ? DEFAULT_LIMIT : Limit.of(limit);
        if (offsetId != null && offsetId > 0) {
            return messageRepository.findMessagesByChatWithOffset(chat, historyInputMessage.getOffsetId(), jpaLimit);
        }
        return messageRepository.findMessagesByChat(chat, jpaLimit);
    }

    private HistoryOutputMessage getOutputHistoryMessage(List<MessageInfo> chatOutputMessages) {
        HistoryOutputMessage historyOutputMessage = new HistoryOutputMessage();
        historyOutputMessage.setMessages(chatOutputMessages);
        return historyOutputMessage;
    }

    private List<MessageInfo> getOutputMessages(Long peerId, List<Message> messages) {
        return messages.stream().map(m -> getMessageInfo(peerId, m)).toList();
    }
}
