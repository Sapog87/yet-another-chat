package ru.sber.yetanotherchat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.dto.Status;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = PeerService.class)
class PeerServiceTest {
    @MockitoBean
    private ChatService chatService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private StatusService statusService;
    @Mock
    private Principal principal;
    @Autowired
    private PeerService peerService;

    private User user;
    private User otherUser;
    private Chat chat;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user1");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("user2");

        chat = new Chat();
        chat.setId(1L);
        chat.setIsGroup(false);

        doReturn(user).when(userService).findUserByUsername(anyString());
        doReturn("user1").when(principal).getName();
    }

    @Test
    @DisplayName("Вернет личный чат")
    void testGetAllChats_withPersonalChat() {
        doReturn(List.of(chat)).when(chatService).findAllChatsByUser(any(User.class));
        doReturn(otherUser).when(userService).findOtherMemberOfPersonalChat(any(User.class), any(Chat.class));
        doReturn(true).when(statusService).isOnline(anyLong());

        var result = peerService.getAllChats(principal);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(otherUser.getId(), result.get(0).getPeerId());
        assertEquals(otherUser.getName(), result.get(0).getPeerName());
        assertEquals(Status.ONLINE, result.get(0).getStatus());

        verify(principal).getName();
        verify(userService).findUserByUsername(user.getName());
        verify(chatService).findAllChatsByUser(user);
        verify(userService).findOtherMemberOfPersonalChat(user, chat);
        verify(statusService).isOnline(otherUser.getId());
    }

    @Test
    @DisplayName("Вернет групповой чат")
    void testGetAllChats_withGroupChat() {
        chat.setIsGroup(true);
        chat.setGroupChatName("group");

        doReturn(List.of(chat)).when(chatService).findAllChatsByUser(any(User.class));

        var result = peerService.getAllChats(principal);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(-chat.getId(), result.get(0).getPeerId());
        assertEquals(chat.getGroupChatName(), result.get(0).getPeerName());
        assertNull(result.get(0).getStatus());

        verify(principal).getName();
        verify(userService).findUserByUsername(user.getName());
        verify(chatService).findAllChatsByUser(user);
    }

    @Test
    @DisplayName("Вернет пустой список")
    void testGetAllChats_noChats() {
        doReturn(List.of()).when(chatService).findAllChatsByUser(user);

        var result = peerService.getAllChats(principal);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(principal).getName();
        verify(userService).findUserByUsername(user.getName());
        verify(chatService).findAllChatsByUser(user);
    }
}