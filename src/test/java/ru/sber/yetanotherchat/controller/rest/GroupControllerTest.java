package ru.sber.yetanotherchat.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.yetanotherchat.dto.GroupDto;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.security.HttpSecurityConfig;
import ru.sber.yetanotherchat.service.GroupService;

import java.security.Principal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {GroupController.class})
@Import(HttpSecurityConfig.class)
@WithMockUser(username = "user")
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @Captor
    private ArgumentCaptor<Principal> principalCaptor;

    @Test
    @DisplayName("Создание группы")
    void createGroup() throws Exception {
        var groupDto = GroupDto.builder().id(-1L).isMember(true).name("test").build();

        doReturn(groupDto)
                .when(groupService)
                .createGroup(anyString(), any(Principal.class));

        mockMvc.perform(post("/api/groups")
                        .param("name", "test"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.peerId").value(-1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.isMember").value(true));

        verify(groupService).createGroup(eq("test"), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Создание группы с пустым параметром")
    void createGroupWithInvalidParams() throws Exception {
        mockMvc.perform(post("/api/groups")
                        .param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск групп с непустым ответом")
    void getGroups() throws Exception {
        var groupDto1 = GroupDto.builder().id(-1L).isMember(true).name("test1").build();
        var groupDto2 = GroupDto.builder().id(-2L).isMember(false).name("test2").build();

        doReturn(List.of(groupDto1, groupDto2))
                .when(groupService)
                .getGroupsByName(anyString(), anyInt(), anyInt(), any(Principal.class));

        mockMvc.perform(get("/api/groups")
                        .param("name", "test")
                        .param("page", "0")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.groups").value(hasSize(2)))
                .andExpect(jsonPath("$.groups[0].peerId").value(-1L))
                .andExpect(jsonPath("$.groups[0].name").value("test1"))
                .andExpect(jsonPath("$.groups[0].isMember").value(true))
                .andExpect(jsonPath("$.groups[1].peerId").value(-2L))
                .andExpect(jsonPath("$.groups[1].name").value("test2"))
                .andExpect(jsonPath("$.groups[1].isMember").value(false));

        verify(groupService).getGroupsByName(eq("test"), eq(0), eq(20), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }


    @Test
    @DisplayName("Поиск групп с пустым ответом")
    void getGroupsWithNoContent() throws Exception {
        doReturn(List.of()).when(groupService).getGroupsByName(anyString(), anyInt(), anyInt(), any(Principal.class));

        mockMvc.perform(get("/api/groups")
                        .param("name", "test"))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.groups").value(hasSize(0)));

        verify(groupService).getGroupsByName(eq("test"), anyInt(), anyInt(), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Поиск групп с пустым параметром")
    void getGroupsWithInvalidParams() throws Exception {
        mockMvc.perform(get("/api/groups")
                        .param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Вступление в группу")
    void participateInGroup() throws Exception {
        doNothing().when(groupService).participateInGroup(any(), any(Principal.class));

        mockMvc.perform(post("/api/groups/{id}/members", -1L))
                .andExpect(status().isOk());

        verify(groupService).participateInGroup(eq(-1L), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Вступление в несуществующую группу")
    void participateInGroupThatNoExists() throws Exception {
        doThrow(PeerNotFoundException.class).when(groupService).participateInGroup(any(), any(Principal.class));

        mockMvc.perform(post("/api/groups/{id}/members", -1L))
                .andExpect(status().isNotFound());

        verify(groupService).participateInGroup(eq(-1L), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("Вступление в группу c невозможным id")
    void participateInGroupWithInvalidParams(long id) throws Exception {
        mockMvc.perform(post("/api/groups/{id}/members", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Выход из группы")
    void leaveGroup() throws Exception {
        doNothing().when(groupService).leaveGroup(any(), any(Principal.class));

        mockMvc.perform(delete("/api/groups/{id}/members", -1L))
                .andExpect(status().isOk());

        verify(groupService).leaveGroup(eq(-1L), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Выход из несуществующей группы")
    void leaveGroupThatNoExists() throws Exception {
        doThrow(PeerNotFoundException.class).when(groupService).leaveGroup(any(), any(Principal.class));

        mockMvc.perform(delete("/api/groups/{id}/members", -1L))
                .andExpect(status().isNotFound());

        verify(groupService).leaveGroup(eq(-1L), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Выход из группы, в которой пользователь не состоит")
    void leaveGroupWhereNotMember() throws Exception {
        doThrow(UnreachablePeerException.class).when(groupService).leaveGroup(any(), any(Principal.class));

        mockMvc.perform(delete("/api/groups/{id}/members", -1L))
                .andExpect(status().isBadRequest());

        verify(groupService).leaveGroup(eq(-1L), principalCaptor.capture());
        assertEquals("user", principalCaptor.getValue().getName());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("Выход из группы c невозможным id")
    void leaveGroupWithInvalidParams(long id) throws Exception {
        mockMvc.perform(delete("/api/groups/{id}/members", id))
                .andExpect(status().isBadRequest());
    }
}