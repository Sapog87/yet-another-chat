package org.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.user.UserCreateDto;
import org.sber.yetanotherchat.dto.user.UserDto;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.exception.UserAlreadyExistsException;
import org.sber.yetanotherchat.exception.UserNotFoundException;
import org.sber.yetanotherchat.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.sber.yetanotherchat.exception.ErrorMessages.USER_WITH_ID_NOT_FOUND;
import static org.sber.yetanotherchat.exception.ErrorMessages.USER_WITH_LOGIN_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserById(Long id) {
        User user = findUserById(id);
        return mapUserToDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        checkIfUserNotExistsByLogin(userCreateDto.getUsername());

        User user = new User();
        user.setName(userCreateDto.getName());
        user.setUsername(userCreateDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userCreateDto.getPassword()));

        User createdUser = userRepository.save(user);

        return mapUserToDto(createdUser);
    }

    private UserDto mapUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        return userDto;
    }

    private User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(
                        USER_WITH_ID_NOT_FOUND.formatted(id))
        );
    }

    private void checkIfUserNotExistsByLogin(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(
                    USER_WITH_LOGIN_ALREADY_EXISTS.formatted(username));
        }
    }
}
