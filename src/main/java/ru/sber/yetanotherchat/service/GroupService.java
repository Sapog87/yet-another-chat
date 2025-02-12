package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.ChatDto;

import java.security.Principal;

public interface GroupService {
    ChatDto createGroup(String name, Principal principal);
}
