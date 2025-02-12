package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.UserAlreadyExistsException;
import ru.sber.yetanotherchat.repository.UserRepository;

import java.util.Set;

import static ru.sber.yetanotherchat.exception.ErrorMessages.USER_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}