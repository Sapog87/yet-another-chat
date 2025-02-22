package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.PeerDto;

import java.security.Principal;
import java.util.List;

/**
 * Интерфейс для работы с чатами (пирами).
 */
public interface PeerService {
    /**
     * Получение всех чатов пользователя.
     * <p>
     * Позволяет получить список всех чатов, в которых участвует указанный пользователь.
     * Это могут быть как личные чаты, так и групповые.
     *
     * @param principal текущий пользователь
     * @return {@link List<PeerDto>} - список чатов
     */
    List<PeerDto> getAllChats(Principal principal);
}
