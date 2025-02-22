package ru.sber.yetanotherchat.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.security.HttpSecurityConfig;
import ru.sber.yetanotherchat.service.MessagingService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MessageController.class})
@Import(HttpSecurityConfig.class)
@WithMockUser(username = "user")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessagingService messagingService;

    @Captor
    private ArgumentCaptor<Principal> principalCaptor;

    @Captor
    private ArgumentCaptor<FetchHistoryDto> fetchHistoryCaptor;

    @Test
    @DisplayName("Поиск сообщений с непустым ответом")
    void historyWithNotEmptyResponse() throws Exception {
        var messageDto = MessageDto.builder()
                .id(1L).peerId(1L)
                .senderName("name1")
                .text("text1")
                .createdAt(LocalDateTime.MIN)
                .outgoing(true).build();

        doReturn(List.of(messageDto)).when(messagingService).fetchHistory(any(FetchHistoryDto.class), any(Principal.class));

        mockMvc.perform(get("/api/messages")
                        .param("peerId", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").value(hasSize(1)))
                .andExpect(jsonPath("$.messages[0].id").value(1L))
                .andExpect(jsonPath("$.messages[0].peerId").value(1L))
                .andExpect(jsonPath("$.messages[0].text").value("text1"))
                .andExpect(jsonPath("$.messages[0].senderName").value("name1"))
                .andExpect(jsonPath("$.messages[0].createdAt").value(LocalDateTime.MIN.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.messages[0].outgoing").value(true));

        verify(messagingService).fetchHistory(fetchHistoryCaptor.capture(), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
        assertEquals(10, fetchHistoryCaptor.getValue().getLimit());
        assertEquals(1L, fetchHistoryCaptor.getValue().getPeerId());
    }

    @Test
    @DisplayName("Поиск сообщений с пустым ответом")
    void historyWithEmptyResponse() throws Exception {
        doReturn(List.of()).when(messagingService).fetchHistory(any(FetchHistoryDto.class), any(Principal.class));

        mockMvc.perform(get("/api/messages")
                        .param("peerId", "1")
                        .param("limit", "10"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.messages").value(hasSize(0)));

        verify(messagingService).fetchHistory(fetchHistoryCaptor.capture(), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
        assertEquals(10, fetchHistoryCaptor.getValue().getLimit());
        assertEquals(1L, fetchHistoryCaptor.getValue().getPeerId());
    }

    @Test
    @DisplayName("Поиск сообщений с неверным параметром")
    void historyWithInvalidParam() throws Exception {
        mockMvc.perform(get("/api/messages")
                        .param("peerId", "")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск сообщений с несуществующим peer")
    void historyWithNotExistedPeer() throws Exception {
        doThrow(PeerNotFoundException.class).when(messagingService).fetchHistory(any(FetchHistoryDto.class), any(Principal.class));

        mockMvc.perform(get("/api/messages")
                        .param("peerId", "1")
                        .param("limit", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Поиск сообщений в группе, в которой пользователь не состоит")
    void historyWithGroupThatNoExists() throws Exception {
        doThrow(UnreachablePeerException.class).when(messagingService).fetchHistory(any(FetchHistoryDto.class), any(Principal.class));

        mockMvc.perform(get("/api/messages")
                        .param("peerId", "1")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());
    }
}