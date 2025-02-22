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
 *
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserService userService;

    /**
     * @param dto
     * @return
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
     * @param name
     * @param page
     * @param pageSize
     * @return
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
     * @param id
     * @return
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