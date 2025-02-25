package ru.sber.yetanotherchat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GroupService.class)
class GroupServiceTest {
    @MockitoBean
    private ChatService chatService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Mock
    private Principal principal;

    private User user;
    private Chat group;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("username");

        group = new Chat();
        group.setId(1L);
        group.setGroupChatName("group");
        group.setIsGroup(true);

        doReturn("username").when(principal).getName();
        doReturn(user).when(userService).findUserByUsername(anyString());
        doReturn(group).when(chatService).findChatById(anyLong());
    }

    @Test
    @DisplayName("Создание группы")
    void createGroup_ShouldReturnGroupDto() {
        doReturn(group).when(chatService).createGroupChat(any(User.class), anyString());

        var result = groupService.createGroup("group", principal);

        assertNotNull(result);
        assertEquals(-1L, result.getId());
        assertEquals("group", result.getName());
        assertTrue(result.getIsMember());

        verify(chatService).createGroupChat(user, "group");
    }

    @Test
    @DisplayName("Возвращение списка групп при поиске по имени")
    void getGroupsByName_ShouldReturnGroupList() {
        doReturn(List.of(group)).when(chatService).findAllGroupsByName(anyString(), anyInt(), anyInt());
        doReturn(true).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));

        var groups = groupService.getGroupsByName("group", 0, 10, principal);

        assertFalse(groups.isEmpty());
        assertEquals(1, groups.size());
        assertEquals("group", groups.get(0).getName());
        assertTrue(groups.get(0).getIsMember());

        verify(chatService).findAllGroupsByName("group", 0, 10);
        verify(chatService).isMemberOfChat(user, group);
    }

    @Test
    @DisplayName("Добавление пользователя в группу")
    void participateInGroup_ShouldAddUserToGroup() {
        doReturn(group).when(chatService).findChatById(anyLong());

        assertDoesNotThrow(() -> groupService.participateInGroup(-1L, principal));
        assertTrue(group.getMembers().contains(user));
        assertTrue(user.getChats().contains(group));

        verify(chatService).findChatById(1L);
    }

    @Test
    @DisplayName("Бросает исключение для неверной группы")
    void participateInGroup_ShouldThrowExceptionForInvalidGroup() {
        doThrow(ChatNotFoundException.class).when(chatService).findChatById(anyLong());

        assertThrows(PeerNotFoundException.class, () -> groupService.participateInGroup(-1L, principal));

        verify(chatService).findChatById(1L);
    }

    @Test
    @DisplayName("Удаляет пользователя из группы")
    void leaveGroup_ShouldRemoveUserFromGroup() {
        group.getMembers().add(user);
        user.getChats().add(group);
        doReturn(true).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));

        assertDoesNotThrow(() -> groupService.leaveGroup(-1L, principal));
        assertFalse(group.getMembers().contains(user));
        assertFalse(user.getChats().contains(group));

        verify(chatService).isMemberOfChat(user, group);
    }

    @Test
    @DisplayName("Бросает исключение, если пользователь не участник группы")
    void leaveGroup_ShouldThrowExceptionIfUserNotMember() {
        doReturn(false).when(chatService).isMemberOfChat(any(User.class), any(Chat.class));

        assertThrows(UnreachablePeerException.class, () -> groupService.leaveGroup(-1L, principal));

        verify(chatService).findChatById(1L);
        verify(chatService).isMemberOfChat(user, group);
    }
}