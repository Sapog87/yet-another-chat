package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.ChatDto;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final ChatService chatService;
    private final UserService userService;

    @Override
    @Transactional
    public ChatDto createGroup(String name, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        var chat = chatService.createGroupChat(user, name);
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getGroupChatName())
                .isGroup(chat.getIsGroup())
                .build();
    }
}