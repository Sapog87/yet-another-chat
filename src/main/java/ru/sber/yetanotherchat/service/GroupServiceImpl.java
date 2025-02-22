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
 * Сервис для работы с группами чатов.
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final ChatService chatService;
    private final UserService userService;

    /**
     * Создает новый групповой чат с указанным названием.
     *
     * @param name      название группы
     * @param principal текущий пользователь, который создает группу
     * @return {@link GroupDto}
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
     * Получает список групп с указанным именем или его частью с пагинацией.
     *
     * @param name      название группы для поиска
     * @param page      страница для пагинации
     * @param pageSize  размер страницы для пагинации
     * @param principal текущий пользователь для проверки членства
     * @return {@link List<GroupDto>}
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
     * Добавляет пользователя в группу
     *
     * @param id        id группы, в которую пользователь хочет вступить
     * @param principal текущий пользователь
     * @throws PeerNotFoundException если группа не найдена
     */
    @Override
    @Transactional
    public void participateInGroup(Long id, Principal principal) {
        var groupId = Math.abs(id);
        var user = userService.findUserByUsername(principal.getName());
        try {
            var group = getGroup(groupId);
            if (!chatService.isMemberOfChat(user, group)) {
                group.getMembers().add(user);
                user.getChats().add(group);
            }
        } catch (ChatNotFoundException e) {
            throw new PeerNotFoundException(
                    "Группа с таким id {%d} не найдена"
                            .formatted(groupId), e);
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
     * Удаляет пользователя иг группы
     *
     * @param id        id группы, из которой пользователь хочет выйти
     * @param principal текущий пользователь
     * @throws UnreachablePeerException если не является членом группы
     * @throws PeerNotFoundException    если группа не найдена
     */
    @Override
    @Transactional
    public void leaveGroup(Long id, Principal principal) {
        var user = userService.findUserByUsername(principal.getName());
        try {
            var groupId = Math.abs(id);
            var group = getGroup(groupId);
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