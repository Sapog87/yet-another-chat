package ru.sber.yetanotherchat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.SendMessageDto;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.MessageService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MessagingService.class)
class MessagingServiceTest {
    @MockitoBean
    private ChatService chatService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MessageService messageService;
    @Mock
    private Principal principal;
    @Autowired
    private MessagingService messagingService;

    private User sender;
    private User recipient;
    private Chat chat;
    private Message message;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setName("sender");
        sender.setUsername("sender");

        recipient = new User();
        recipient.setId(2L);
        recipient.setName("recipient");
        recipient.setUsername("recipient");

        chat = new Chat();
        chat.setId(1L);

        message = new Message();
        message.setId(1L);
        message.setSender(sender);
        message.setChat(chat);
        message.setText("Hello");
        message.setCreatedAt(LocalDateTime.MIN);

        doReturn(sender.getUsername()).when(principal).getName();
        doReturn(sender).when(userService).findUserByUsername(anyString());
    }

    @Test
    @DisplayName("Успешная отправка сообщения пользователю")
    void sendMessageToUser_Success() {
        chat.setIsGroup(false);

        doReturn(recipient).when(userService).findUserById(anyLong());
        doReturn(chat).when(chatService).findOrCreatePersonalChat(any(User.class), any(User.class));
        doReturn(message).when(messageService).createMessage(any(User.class), any(Chat.class), anyString());
        doReturn(List.of(sender, recipient)).when(userService).findChatMembers(any(Chat.class));

        var sendMessageDto = new SendMessageDto();
        sendMessageDto.setPeerId(recipient.getId());
        sendMessageDto.setText("Hello");

        var result = messagingService.sendMessage(sendMessageDto, principal);

        assertNotNull(result);
        assertEquals(message.getId(), result.getId());
        assertEquals(message.getText(), result.getText());
        assertEquals(recipient.getId(), result.getPeerId());
        assertEquals(sender.getName(), result.getSenderName());
        assertEquals(message.getCreatedAt(), result.getCreatedAt());
        assertEquals(true, result.getOutgoing());
    }

    @Test
    @DisplayName("Ошибка при отправке сообщения: пользователь не найден")
    void sendMessageToUser_UserNotFound() {
        chat.setIsGroup(false);

        doThrow(UserNotFoundException.class).when(userService).findUserById(anyLong());

        var sendMessageDto = new SendMessageDto();
        sendMessageDto.setPeerId(recipient.getId());
        sendMessageDto.setText("Hello");

        assertThrows(PeerNotFoundException.class, () -> messagingService.sendMessage(sendMessageDto, principal));
    }


    @Test
    @DisplayName("Успешная отправка сообщения в группу")
    void sendMessageToGroup_Success() {
        chat.setIsGroup(true);

        doReturn(recipient).when(userService).findUserById(anyLong());
        doReturn(chat).when(chatService).findChatById(anyLong());
        doReturn(true).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));
        doReturn(message).when(messageService).createMessage(any(User.class), any(Chat.class), anyString());
        doReturn(List.of(sender, recipient)).when(userService).findChatMembers(any(Chat.class));

        var sendMessageDto = new SendMessageDto();
        sendMessageDto.setPeerId(-chat.getId());
        sendMessageDto.setText("Hello");

        var result = messagingService.sendMessage(sendMessageDto, principal);

        assertNotNull(result);
        assertEquals(message.getId(), result.getId());
        assertEquals(message.getText(), result.getText());
        assertEquals(-chat.getId(), result.getPeerId());
        assertEquals(sender.getName(), result.getSenderName());
        assertEquals(message.getCreatedAt(), result.getCreatedAt());
        assertEquals(true, result.getOutgoing());
    }

    @Test
    @DisplayName("Ошибка при отправке сообщения в группу: группа не найдена")
    void sendMessageToGroup_ChatNotFound() {
        chat.setIsGroup(false);

        doReturn(chat).when(chatService).findChatById(anyLong());
        doThrow(UserNotFoundException.class).when(userService).findUserById(anyLong());

        var sendMessageDto = new SendMessageDto();
        sendMessageDto.setPeerId(-chat.getId());
        sendMessageDto.setText("Hello");

        assertThrows(PeerNotFoundException.class, () -> messagingService.sendMessage(sendMessageDto, principal));
    }

    @Test
    @DisplayName("Ошибка при отправке сообщения в группу: не является членом группы")
    void sendMessageToGroup_UnreachablePeer() {
        chat.setIsGroup(true);

        doReturn(chat).when(chatService).findChatById(anyLong());
        doReturn(false).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));

        var sendMessageDto = new SendMessageDto();
        sendMessageDto.setPeerId(-chat.getId());
        sendMessageDto.setText("Hello");

        assertThrows(UnreachablePeerException.class, () -> messagingService.sendMessage(sendMessageDto, principal));
    }

    @Test
    @DisplayName("Успешное получение сообщений с пользователем")
    void fetchHistoryWithUser_Success() {
        chat.setIsGroup(false);

        doReturn(recipient).when(userService).findUserById(anyLong());
        doReturn(chat).when(chatService).findPersonalChat(any(User.class), any(User.class));
        doReturn(List.of(message)).when(messageService).fetchMessagesFromChat(any(Chat.class), anyInt(), eq(null));

        var fetchHistoryDto = FetchHistoryDto.builder()
                .peerId(recipient.getId())
                .limit(0)
                .offsetId(null)
                .build();

        var result = messagingService.fetchHistory(fetchHistoryDto, principal);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(message.getId(), result.get(0).getId());
        assertEquals(message.getText(), result.get(0).getText());
        assertEquals(recipient.getId(), result.get(0).getPeerId());
        assertEquals(sender.getName(), result.get(0).getSenderName());
        assertEquals(message.getCreatedAt(), result.get(0).getCreatedAt());
        assertEquals(true, result.get(0).getOutgoing());
    }

    @Test
    @DisplayName("Ошибка получения сообщений с пользователем: пользователь не найден")
    void fetchHistoryWithUser_UserNotFound() {
        doThrow(UserNotFoundException.class).when(userService).findUserById(anyLong());

        var fetchHistoryDto = FetchHistoryDto.builder()
                .peerId(recipient.getId())
                .limit(0)
                .offsetId(null)
                .build();

        assertThrows(PeerNotFoundException.class, () -> messagingService.fetchHistory(fetchHistoryDto, principal));
    }

    @Test
    @DisplayName("Успешное получение сообщений из группы")
    void fetchHistoryWithGroup_Success() {
        chat.setIsGroup(true);

        doReturn(List.of(message)).when(messageService).fetchMessagesFromChat(any(Chat.class), anyInt(), eq(null));
        doReturn(chat).when(chatService).findChatById(anyLong());
        doReturn(true).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));

        var fetchHistoryDto = FetchHistoryDto.builder()
                .peerId(-chat.getId())
                .limit(0)
                .offsetId(null)
                .build();

        var result = messagingService.fetchHistory(fetchHistoryDto, principal);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(message.getId(), result.get(0).getId());
        assertEquals(message.getText(), result.get(0).getText());
        assertEquals(-chat.getId(), result.get(0).getPeerId());
        assertEquals(sender.getName(), result.get(0).getSenderName());
        assertEquals(message.getCreatedAt(), result.get(0).getCreatedAt());
        assertEquals(true, result.get(0).getOutgoing());
    }

    @Test
    @DisplayName("Ошибка при получении сообщений из группы: группа не найдена")
    void fetchHistoryWithGroup_ChatNotFound() {
        doThrow(ChatNotFoundException.class).when(chatService).findChatById(anyLong());

        var fetchHistoryDto = FetchHistoryDto.builder()
                .peerId(-chat.getId())
                .limit(0)
                .offsetId(null)
                .build();

        assertThrows(PeerNotFoundException.class, () -> messagingService.fetchHistory(fetchHistoryDto, principal));
    }

    @Test
    @DisplayName("Ошибка при получении сообщений из группы: не является членом группы")
    void fetchHistoryWithGroup_UnreachablePeer() {
        chat.setIsGroup(true);

        doReturn(chat).when(chatService).findChatById(anyLong());
        doReturn(false).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));


        var sendMessageDto = new SendMessageDto();
        sendMessageDto.setPeerId(-chat.getId());
        sendMessageDto.setText("Hello");

        assertThrows(UnreachablePeerException.class, () -> messagingService.sendMessage(sendMessageDto, principal));
    }
}