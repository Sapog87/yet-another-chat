package ru.sber.yetanotherchat.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.yetanotherchat.dto.Status;
import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.security.BaseHttpSecurityConfig;
import ru.sber.yetanotherchat.service.AccountService;
import ru.sber.yetanotherchat.service.StatusService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class})
@Import(BaseHttpSecurityConfig.class)
@WithMockUser(username = "user", authorities = "USER")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private StatusService statusService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = UserDto.builder().id(1L).name("user1").build();
        user2 = UserDto.builder().id(2L).name("user2").build();
    }

    @Test
    @DisplayName("Поиск пользователей с непустым ответом")
    void getUsersWithNotEmptyResponse() throws Exception {
        doReturn(List.of(user1, user2)).when(accountService).getUsersByName(anyString(), anyInt(), anyInt());

        doReturn(true).doReturn(false).when(statusService).isOnline(anyLong());

        mockMvc.perform(get("/api/users")
                        .param("name", "user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].peerId").value(1L))
                .andExpect(jsonPath("$.[0].name").value("user1"))
                .andExpect(jsonPath("$.[0].status").value(Status.ONLINE.name()))
                .andExpect(jsonPath("$.[1].peerId").value(2L))
                .andExpect(jsonPath("$.[1].name").value("user2"))
                .andExpect(jsonPath("$.[1].status").value(Status.OFFLINE.name()));

        verify(accountService).getUsersByName(eq("user"), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Поиск пользователей с пустым ответом")
    void getUsersWithEmptyResponse() throws Exception {
        doReturn(List.of()).when(accountService).getUsersByName(anyString(), anyInt(), anyInt());

        mockMvc.perform(get("/api/users")
                        .param("name", "user"))
                .andExpect(status().isNoContent());

        verify(accountService).getUsersByName(eq("user"), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Поиск пользователей с неверным параметром")
    void getUsersWithInvalidParam() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск пользователя с непустым ответом")
    void getUserWithNotEmptyResponse() throws Exception {
        doReturn(user1).when(accountService).getUserById(anyLong());
        doReturn(true).when(statusService).isOnline(anyLong());

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(accountService).getUserById(1L);
    }

    @Test
    @DisplayName("Поиск пользователя с неверным параметром")
    void getUserWithInvalidParam() throws Exception {
        mockMvc.perform(get("/api/users/{id}", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск пользователя, которого не существует")
    void getUserThatNotExists() throws Exception {
        doThrow(PeerNotFoundException.class).when(accountService).getUserById(anyLong());

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(accountService).getUserById(1L);
    }
}