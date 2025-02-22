package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;
import ru.sber.yetanotherchat.exception.PeerNotFoundException;
import ru.sber.yetanotherchat.exception.UserAlreadyExistsException;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.util.List;

/**
 * Интерфейс для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserService userService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param dto данные для регистрации нового пользователя
     * @return {@link UserDto}
     * @throws UserAlreadyExistsException если пользователь с таким username уже существует
     */
    @Override
    @Transactional
    public UserDto registerUser(UserRegistrationDto dto) {
        if (userService.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Пользователь с таким username {%s} уже существует"
                            .formatted(dto.getUsername()));
        }

        var user = userService.createUser(
                dto.getUsername(),
                dto.getPassword(),
                dto.getName());

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    /**
     * Находит пользователей по части имени с пагинацией.
     *
     * @param name     имя для поиска пользователей
     * @param page     номер страницы для пагинации
     * @param pageSize размер страницы
     * @return {@link List<UserDto>}
     */
    @Override
    public List<UserDto> getUsersByName(String name, Integer page, Integer pageSize) {
        var users = userService.findAllUsersByName(name, page, pageSize);

        return users.stream().map(user -> UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .build()
        ).toList();
    }

    /**
     * Находит пользователя по его id.
     *
     * @param id идентификатор пользователя
     * @return {@link UserDto}
     * @throws PeerNotFoundException если пользователя не существует
     */
    @Override
    public UserDto getUserById(Long id) {
        try {
            var user = userService.findUserById(id);

            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .username(user.getUsername())
                    .build();
        } catch (UserNotFoundException e) {
            throw new PeerNotFoundException(e.getMessage(), e);
        }
    }
}