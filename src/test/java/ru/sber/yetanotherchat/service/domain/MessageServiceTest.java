package ru.sber.yetanotherchat.service.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.repository.MessageRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MessageService.class)
class MessageServiceTest {

    @MockitoBean
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    private User sender;
    private Chat chat;

    @BeforeEach
    void setUp() {
        sender = new User();
        chat = new Chat();
    }

    @Test
    @DisplayName("Успешное создание сообщения")
    void createMessage_ShouldSaveAndReturnMessage() {
        var message = new Message();
        message.setSender(sender);
        message.setChat(chat);
        message.setText("text");

        doReturn(message)
                .when(messageRepository)
                .save(any(Message.class));

        var result = messageService.createMessage(sender, chat, "text");

        assertNotNull(result);
        assertEquals("text", result.getText());
        assertEquals(sender, result.getSender());
        assertEquals(chat, result.getChat());

        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("Успешное получение сообщений с offset")
    void fetchMessagesFromChat_WithOffset_ShouldReturnMessages() {
        var message = new Message();
        doReturn(List.of(message))
                .when(messageRepository)
                .findMessagesByChatAndIdLessThanOrderByIdDesc(eq(chat), anyLong(), any());

        var result = messageService.fetchMessagesFromChat(chat, 10, 100L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(messageRepository, times(1))
                .findMessagesByChatAndIdLessThanOrderByIdDesc(eq(chat), eq(100L), any());
    }

    @Test
    @DisplayName("Успешное получение сообщений без offset")
    void fetchMessagesFromChat_WithoutOffset_ShouldReturnMessages() {
        var message = new Message();
        when(messageRepository.findMessagesByChatOrderByIdDesc(eq(chat), any())).thenReturn(List.of(message));

        var result = messageService.fetchMessagesFromChat(chat, 10, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(messageRepository, times(1))
                .findMessagesByChatOrderByIdDesc(eq(chat), any());
    }

    @Test
    @DisplayName("Использование стандартного limit, если передан 0")
    void fetchMessagesFromChat_WithInvalidLimit_ShouldUseDefaultLimit() {
        messageService.fetchMessagesFromChat(chat, 0, null);

        verify(messageRepository, times(1))
                .findMessagesByChatOrderByIdDesc(eq(chat), any());
    }
}