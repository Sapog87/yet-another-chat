package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.GroupDto;

import java.security.Principal;
import java.util.List;

/**
 *
 */
public interface GroupService {
    /**
     * @param name
     * @param principal
     * @return
     */
    GroupDto createGroup(String name, Principal principal);

    /**
     * @param name
     * @param page
     * @param pageSize
     * @param principal
     * @return
     */
    List<GroupDto> getGroupsByName(String name, Integer page, Integer pageSize, Principal principal);

    /**
     * @param id
     * @param principal
     */
    void participateInGroup(Long id, Principal principal);

    /**
     * @param chatId
     * @param principal
     */
    void leaveGroup(Long chatId, Principal principal);
}
