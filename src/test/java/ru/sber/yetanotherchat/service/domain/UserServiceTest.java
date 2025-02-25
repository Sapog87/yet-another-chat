package ru.sber.yetanotherchat.service.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Role;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.repository.RoleRepository;
import ru.sber.yetanotherchat.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserService.class)
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setName("user");
        user.setPasswordHash("encodedPassword");
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser_ShouldReturnCreatedUser() {
        var role = new Role();
        role.setRole(Role.UserRole.USER);

        doReturn("encodedPassword").when(passwordEncoder).encode("password");
        doReturn(role).when(roleRepository).findByRole(Role.UserRole.USER);
        doReturn(user).when(userRepository).save(any(User.class));

        var createdUser = userService.createUser("user", "password", "user");

        assertNotNull(createdUser);
        assertEquals("user", createdUser.getUsername());
        assertEquals("user", createdUser.getName());
        assertEquals("encodedPassword", createdUser.getPasswordHash());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Проверка существования пользователя")
    void existsByUsername_ShouldReturnTrueIfUserExists() {
        doReturn(true).when(userRepository).existsByUsername("user");

        var exists = userService.existsByUsername("user");

        assertTrue(exists);
        verify(userRepository).existsByUsername("user");
    }

    @Test
    @DisplayName("Успешный поиск пользователя по имени")
    void findUserByUsername_ShouldReturnUserIfExists() {
        doReturn(Optional.of(user)).when(userRepository).findByUsername("user");

        var foundUser = userService.findUserByUsername("user");

        assertEquals(user, foundUser);
        verify(userRepository).findByUsername("user");
    }

    @Test
    @DisplayName("Поиск пользователя по имени бросает исключение")
    void findUserByUsername_ShouldThrowExceptionIfUserNotFound() {
        doReturn(Optional.empty()).when(userRepository).findByUsername("unknown");

        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername("unknown"));
    }

    @Test
    @DisplayName("Успешный поиск пользователя по id")
    void findUserById_ShouldReturnUserIfExists() {
        doReturn(Optional.of(user)).when(userRepository).findById(1L);

        var foundUser = userService.findUserById(1L);

        assertEquals(user, foundUser);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Поиск пользователя по id бросает исключение")
    void findUserById_ShouldThrowExceptionIfUserNotFound() {
        doReturn(Optional.empty()).when(userRepository).findById(1L);

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    @DisplayName("Успешный поиск пользователей по имени")
    void findAllUsersByName_ShouldReturnUserList() {
        doReturn(List.of(user)).when(userRepository).findAllByNameContainingIgnoreCase(anyString(), any(PageRequest.class));

        var users = userService.findAllUsersByName("test", 0, 20);

        assertEquals(user, users.get(0));
        verify(userRepository).findAllByNameContainingIgnoreCase(eq("test"), any(PageRequest.class));
    }

    @Test
    @DisplayName("Успешный поиск участников чата")
    void findChatMembers_ShouldReturnChatMembers() {
        var chat = new Chat();
        doReturn(List.of(user)).when(userRepository).findAllByChats(chat);

        var members = userService.findChatMembers(chat);

        assertEquals(user, members.get(0));
        verify(userRepository).findAllByChats(chat);
    }

    @Test
    @DisplayName("Успешный поиск второго участника личного чата")
    void testFindOtherMemberOfPersonalChat_ShouldReturnChatMember() {
        var user1 = mock(User.class);
        var chat = mock(Chat.class);

        doReturn(false).when(chat).getIsGroup();
        doReturn(List.of(user, user1)).when(chat).getMembers();

        var otherMember = userService.findOtherMemberOfPersonalChat(user1, chat);
        assertEquals(user, otherMember);
    }

    @Test
    @DisplayName("Поиск второго участника личного чата, тк чат является группой")
    void testFindOtherMemberOfPersonalChat_ShouldThrowIllegalArgumentException() {
        var chat = mock(Chat.class);

        doReturn(true).when(chat).getIsGroup();

        assertThrows(IllegalArgumentException.class,
                () -> userService.findOtherMemberOfPersonalChat(user, chat));
    }

    @Test
    @DisplayName("Поиск второго участника личного чата, тк у чата нет участников")
    void testFindOtherMemberOfPersonalChat_ShouldThrowUserNotFoundException() {
        var chat = mock(Chat.class);

        doReturn(false).when(chat).getIsGroup();
        doReturn(List.of()).when(chat).getMembers();

        assertThrows(UserNotFoundException.class,
                () -> userService.findOtherMemberOfPersonalChat(user, chat));
    }

    @Test
    @DisplayName("Успешный поиск второго участника личного чата, в случае чата с самим собой")
    void testFindOtherMemberOfPersonalChat_ShouldReturnSameUser() {
        var chat = mock(Chat.class);

        doReturn(false).when(chat).getIsGroup();
        doReturn(List.of(user)).when(chat).getMembers();

        var otherMember = userService.findOtherMemberOfPersonalChat(user, chat);
        assertEquals(user, otherMember);
    }
}