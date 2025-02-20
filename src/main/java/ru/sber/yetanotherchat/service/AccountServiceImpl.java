package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.ResourceNotFoundException;
import ru.sber.yetanotherchat.exception.UserAlreadyExistsException;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.repository.UserRepository;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.util.List;
import java.util.Set;

import static ru.sber.yetanotherchat.exception.ErrorMessages.USER_ALREADY_EXISTS;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    /**
     * @param userRegistrationDto
     * @return
     */
    @Override
    @Transactional
    public UserDto registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByUsername(userRegistrationDto.getUsername())) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setName(userRegistrationDto.getName());
        user.setUsername(userRegistrationDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setRoles(Set.of(User.Role.USER));

        User createdUser = userRepository.save(user);

        return UserDto.builder()
                .id(createdUser.getId())
                .username(createdUser.getUsername())
                .name(createdUser.getName())
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
    public UserDto getUsersById(Long id) {
        try {
            var user = userService.findUserById(id);

            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .username(user.getUsername())
                    .build();
        } catch (UserNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }
    }
}