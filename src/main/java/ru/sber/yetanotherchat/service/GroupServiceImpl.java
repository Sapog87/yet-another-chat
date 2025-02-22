package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.GroupDto;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UnreachablePeerException;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final ChatService chatService;
    private final UserService userService;

    /**
     * @param name
     * @param principal
     * @return
     */
    @Override
    @Transactional
    public GroupDto createGroup(String name, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        var chat = chatService.createGroupChat(user, name);
        return GroupDto.builder()
                .id(-chat.getId())
                .name(chat.getGroupChatName())
                .isMember(true)
                .build();
    }

    /**
     * @param name
     * @param page
     * @param pageSize
     * @param principal
     * @return
     */
    @Override
    @Transactional
    public List<GroupDto> getGroupsByName(String name, Integer page, Integer pageSize, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        var groups = chatService.findAllGroupsByName(name, page, pageSize);
        return groups.stream()
                .map(group -> GroupDto.builder()
                        .id(-group.getId())
                        .name(group.getGroupChatName())
                        .isMember(chatService.isMemberOfChat(user, group))
                        .build()
                ).toList();
    }

    /**
     * @param groupId
     * @param principal
     */
    @Override
    @Transactional
    public void participateInGroup(Long groupId, Principal principal) {
        var id = Math.abs(groupId);
        var user = userService.findUserByUsername(principal.getName());
        try {
            var group = getGroup(id);
            if (!chatService.isMemberOfChat(user, group)) {
                group.getMembers().add(user);
                user.getChats().add(group);
            }
        } catch (ChatNotFoundException e) {
            throw new PeerNotFoundException(
                    "Группа с таким id {%d} не найдена"
                            .formatted(id), e);
        }
    }

    private Chat getGroup(Long id) {
        var group = chatService.findChatById(id);
        if (Boolean.FALSE.equals(group.getIsGroup())) {
            throw new PeerNotFoundException(
                    "Группа с таким id {%d} не найдена"
                            .formatted(id));
        }
        return group;
    }

    /**
     * @param groupId
     * @param principal
     */
    @Override
    @Transactional
    public void leaveGroup(Long groupId, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        try {
            var id = Math.abs(groupId);
            var group = getGroup(id);
            if (!chatService.isMemberOfChat(user, group)) {
                throw new UnreachablePeerException(
                        "Пользователь {%d} не может покинуть группу {%d} в которой не состоит"
                                .formatted(user.getId(), groupId));
            }
            user.getChats().remove(group);
            group.getMembers().remove(user);
        } catch (ChatNotFoundException e) {
            throw new PeerNotFoundException(e.getMessage(), e);
        }
    }
}