package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;

import java.util.List;

/**
 * Интерфейс для работы с пользователями.
 */
public interface AccountService {
    /**
     * Регистрирует нового пользователя.
     *
     * @param dto данные для регистрации нового пользователя
     * @return {@link UserDto}
     */
    UserDto registerUser(UserRegistrationDto dto);

    /**
     * Находит пользователей по части имени с пагинацией.
     *
     * @param name     имя для поиска пользователей
     * @param page     номер страницы для пагинации
     * @param pageSize размер страницы
     * @return {@link List<UserDto>}
     */
    List<UserDto> getUsersByName(String name, Integer page, Integer pageSize);

    /**
     * Находит пользователя по его id.
     *
     * @param id идентификатор пользователя
     * @return {@link UserDto}
     */
    UserDto getUserById(Long id);
}
