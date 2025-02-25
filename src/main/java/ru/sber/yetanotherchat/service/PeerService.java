package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.PeerDto;
import ru.sber.yetanotherchat.dto.Status;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.service.domain.ChatService;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.security.Principal;
import java.util.List;

import static ru.sber.yetanotherchat.dto.Status.OFFLINE;
import static ru.sber.yetanotherchat.dto.Status.ONLINE;

/**
 * Сервис для работы с чатами (пирами).
 */
@Service
@RequiredArgsConstructor
public class PeerService {
    private final ChatService chatService;
    private final UserService userService;
    private final StatusService statusService;

    /**
     * Получение всех чатов пользователя.
     * <p>
     * Позволяет получить список всех чатов, в которых участвует указанный пользователь.
     * Это могут быть как личные чаты, так и групповые.
     *
     * @param principal текущий пользователь
     * @return {@link List<PeerDto>} - список чатов
     */
    @Transactional
    public List<PeerDto> getAllChats(Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        var chats = chatService.findAllChatsByUser(user);
        return chats.stream()
                .map(chat -> {
                    if (Boolean.TRUE.equals(chat.getIsGroup())) {
                        return PeerDto.builder()
                                .peerId(-chat.getId())
                                .peerName(chat.getGroupChatName())
                                .build();
                    }
                    var otherUser = userService.findOtherMemberOfPersonalChat(user, chat);
                    return PeerDto.builder()
                            .peerId(otherUser.getId())
                            .peerName(otherUser.getName())
                            .status(getStatus(user, otherUser))
                            .build();
                })
                .toList();
    }

    private Status getStatus(User user, User otherUser) {
        if (user.equals(otherUser)) {
            return ONLINE;
        }
        return statusService.isOnline(otherUser.getId()) ? ONLINE : OFFLINE;
    }
}
