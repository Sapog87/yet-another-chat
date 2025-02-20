package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.GroupDto;
import ru.sber.yetanotherchat.exception.AccessDeniedException;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.exception.InvalidPeerException;
import ru.sber.yetanotherchat.exception.ResourceNotFoundException;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

import static ru.sber.yetanotherchat.exception.ErrorMessages.INVALID_PEER;
import static ru.sber.yetanotherchat.exception.ErrorMessages.PEER_NOT_MEMBER;

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
                .id(chat.getId())
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
        return groups.stream().map(group -> GroupDto.builder()
                .id(group.getId())
                .name(group.getGroupChatName())
                .isMember(chatService.isMemberOfChat(user, group))
                .build()
        ).toList();
    }

    /**
     * @param id
     * @param principal
     */
    @Override
    @Transactional
    public void participateInGroup(Long id, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        try {
            var group = chatService.findChatById(id);
            if (Boolean.FALSE.equals(group.getIsGroup())) {
                throw new InvalidPeerException(INVALID_PEER);
            }
            group.getMembers().add(user);
            user.getChats().add(group);
        } catch (ChatNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * @param chatId
     * @param principal
     */
    @Override
    @Transactional
    public void leaveGroup(Long chatId, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        try {
            var group = chatService.findChatById(chatId);
            if (Boolean.FALSE.equals(group.getIsGroup())) {
                throw new InvalidPeerException(INVALID_PEER);
            }
            if (!chatService.isMemberOfChat(user, group)) {
                throw new AccessDeniedException(PEER_NOT_MEMBER);
            }
            user.getChats().remove(group);
            group.getMembers().remove(user);
        } catch (ChatNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }
    }
}