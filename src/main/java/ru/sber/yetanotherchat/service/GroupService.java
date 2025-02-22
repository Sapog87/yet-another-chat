package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.GroupDto;

import java.security.Principal;
import java.util.List;

/**
 * Интерфейс сервиса для работы с группами чатов.
 */
public interface GroupService {
    /**
     * Создает новый групповой чат с указанным названием.
     *
     * @param name      название группы
     * @param principal текущий пользователь, который создает группу
     * @return {@link GroupDto}
     */
    GroupDto createGroup(String name, Principal principal);

    /**
     * Получает список групп с указанным именем или его частью с пагинацией.
     *
     * @param name      название группы для поиска
     * @param page      страница для пагинации
     * @param pageSize  размер страницы для пагинации
     * @param principal текущий пользователь для проверки членства
     * @return {@link List<GroupDto>}
     */
    List<GroupDto> getGroupsByName(String name, Integer page, Integer pageSize, Principal principal);

    /**
     * Добавляет пользователя в группу
     *
     * @param id        id группы, в которую пользователь хочет вступить
     * @param principal текущий пользователь
     */
    void participateInGroup(Long id, Principal principal);

    /**
     * Удаляет пользователя иг группы
     *
     * @param id    id группы, из которой пользователь хочет выйти
     * @param principal текущий пользователь
     */
    void leaveGroup(Long id, Principal principal);
}
