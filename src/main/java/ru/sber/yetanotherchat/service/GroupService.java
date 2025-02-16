package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.ChatDto;

import java.security.Principal;
import java.util.List;

public interface GroupService {
    ChatDto createGroup(String name, Principal principal);

    List<ChatDto> getGroupsByName(String name);
}
