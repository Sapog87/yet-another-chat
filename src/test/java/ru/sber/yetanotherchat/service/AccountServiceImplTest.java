package ru.sber.yetanotherchat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UserAlreadyExistsException;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AccountServiceImpl.class)
class AccountServiceImplTest {
    @MockitoBean
    private UserService userService;
    @Autowired
    private AccountService accountService;

    private User user;
    private UserRegistrationDto userRegistrationDto;

    @BeforeEach
    void setUp() {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setName("name");
        userRegistrationDto.setUsername("username");
        userRegistrationDto.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setName("name");
        user.setPasswordHash("encodedPassword");
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void registerUser_Success() {
        doReturn(false).when(userService).existsByUsername(anyString());
        doReturn(user).when(userService).createUser(anyString(), anyString(), anyString());

        var result = accountService.registerUser(userRegistrationDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getName(), result.getName());

        verify(userService).createUser("username", "password", "name");
    }

    @Test
    @DisplayName("Ошибка при регистрации: пользователь уже существует")
    void registerUser_UserAlreadyExists() {
        doReturn(true).when(userService).existsByUsername(anyString());

        assertThrows(UserAlreadyExistsException.class, () -> accountService.registerUser(userRegistrationDto));

        verify(userService).existsByUsername("username");
    }

    @Test
    @DisplayName("Успешное получение пользователя по ID")
    void getUserById_Success() {
        doReturn(user).when(userService).findUserById(1L);

        var result = accountService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    @DisplayName("Ошибка при получении пользователя по ID: пользователь не найден")
    void getUserById_UserNotFound() {
        doThrow(PeerNotFoundException.class).when(userService).findUserById(1L);

        assertThrows(PeerNotFoundException.class, () -> accountService.getUserById(1L));
    }

    @Test
    @DisplayName("Успешный поиск пользователей по имени")
    void getUsersByName_Success() {
        doReturn(List.of(user)).when(userService).findAllUsersByName(anyString(), anyInt(), anyInt());

        var result = accountService.getUsersByName("name", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user.getUsername(), result.get(0).getUsername());
        assertEquals(user.getName(), result.get(0).getName());
        verify(userService).findAllUsersByName("name", 0, 10);
    }
}