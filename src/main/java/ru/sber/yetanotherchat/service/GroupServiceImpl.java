package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.ChatDto;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

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

    @Override
    public List<ChatDto> getGroupsByName(String name) {
        var groups = chatService.findAllGroupsByName(name);
        return groups.stream().map(group -> ChatDto.builder()
                .id(group.getId())
                .isGroup(group.getIsGroup())
                .name(group.getGroupChatName())
                .build()
        ).toList();
    }
}