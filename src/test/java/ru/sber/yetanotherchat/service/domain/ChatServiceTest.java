package ru.sber.yetanotherchat.service.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.entity.UserChat;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.repository.ChatRepository;
import ru.sber.yetanotherchat.repository.UserChatRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ChatService.class)
class ChatServiceTest {

    @MockitoBean
    private ChatRepository chatRepository;

    @MockitoBean
    private UserChatRepository userChatRepository;

    @Autowired
    private ChatService chatService;

    private User user;
    private User recipient;
    private Chat chat;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        recipient = new User();
        recipient.setId(2L);
        chat = new Chat();
        chat.setId(1L);
    }

    @Test
    @DisplayName("Должен вернуть чат, если он уже существует")
    void testFindOrCreatePersonalChatWhenChatExists() {
        doReturn(Optional.of(chat)).when(chatRepository).findPersonalChatByUsers(user, recipient);

        var result = chatService.findOrCreatePersonalChat(user, recipient);

        assertEquals(chat, result);
        verify(chatRepository, times(1)).findPersonalChatByUsers(user, recipient);
    }

    @Test
    @DisplayName("Должен создать новый чат, если чата не существует")
    void testFindOrCreatePersonalChatWhenChatDoesNotExist() {
        doReturn(Optional.empty()).when(chatRepository).findPersonalChatByUsers(user, recipient);
        doReturn(chat).when(chatRepository).save(any(Chat.class));
        doReturn(new UserChat()).when(userChatRepository).save(any(UserChat.class));

        var result = chatService.findOrCreatePersonalChat(user, recipient);

        assertEquals(chat, result);
        verify(chatRepository, times(1)).findPersonalChatByUsers(user, recipient);
        verify(chatRepository, times(1)).save(any(Chat.class));
        verify(userChatRepository, times(2)).save(any(UserChat.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если чат не найден")
    void testFindPersonalChatThrowsExceptionWhenChatNotFound() {
        doReturn(Optional.empty()).when(chatRepository).findPersonalChatByUsers(user, recipient);

        assertThrows(ChatNotFoundException.class, () -> chatService.findPersonalChat(user, recipient));
    }

    @Test
    @DisplayName("Должен вернуть чат по id")
    void testFindChatById() {
        doReturn(Optional.of(chat)).when(chatRepository).findById(1L);

        var result = chatService.findChatById(1L);

        assertEquals(chat, result);
        verify(chatRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если чат по id не найден")
    void testFindChatByIdThrowsExceptionWhenChatNotFound() {
        doReturn(Optional.empty()).when(chatRepository).findById(1L);

        assertThrows(ChatNotFoundException.class, () -> chatService.findChatById(1L));
    }

    @Test
    @DisplayName("Должен вернуть true, если пользователь является участником чата")
    void testIsMemberOfChatWhenUserIsMember() {
        doReturn(true).when(userChatRepository).existsByChatAndUser(chat, user);

        var result = chatService.isMemberOfChat(user, chat);

        assertTrue(result);
        verify(userChatRepository, times(1)).existsByChatAndUser(chat, user);
    }

    @Test
    @DisplayName("Должен вернуть false, если пользователь не является участником чата")
    void testIsMemberOfChatWhenUserIsNotMember() {
        doReturn(false).when(userChatRepository).existsByChatAndUser(chat, user);

        var result = chatService.isMemberOfChat(user, chat);

        assertFalse(result);
        verify(userChatRepository, times(1)).existsByChatAndUser(chat, user);
    }

    @Test
    @DisplayName("Должен создать групповой чат")
    void testCreateGroupChat() {
        doReturn(chat).when(chatRepository).save(any(Chat.class));
        doReturn(new UserChat()).when(userChatRepository).save(any(UserChat.class));

        var result = chatService.createGroupChat(user, "name");

        assertEquals(chat, result);
        verify(chatRepository, times(1)).save(any(Chat.class));
        verify(userChatRepository, times(1)).save(any(UserChat.class));
    }

    @Test
    @DisplayName("Должен вернуть все группы по имени с пагинацией")
    void testFindAllGroupsByName() {
        doReturn(List.of(chat)).when(chatRepository)
                .findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(
                        anyString(), eq(true), any(PageRequest.class));

        var result = chatService.findAllGroupsByName("name", 0, 10);

        assertFalse(result.isEmpty());
        verify(chatRepository, times(1))
                .findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(
                        eq("name"), eq(true), any(PageRequest.class));
    }

    @Test
    @DisplayName("Должен вернуть все чаты пользователя")
    void testFindAllChatsByUser() {
        var userChat = new UserChat();
        userChat.setChat(chat);
        doReturn(List.of(userChat)).when(userChatRepository).findAllByUser(user);

        var result = chatService.findAllChatsByUser(user);

        assertEquals(1, result.size());
        assertEquals(chat, result.get(0));
        verify(userChatRepository, times(1)).findAllByUser(user);
    }
}