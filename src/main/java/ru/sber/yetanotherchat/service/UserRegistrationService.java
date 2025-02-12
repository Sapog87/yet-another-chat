package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.UserRegistrationDto;
import ru.sber.yetanotherchat.dto.UserDto;

public interface UserRegistrationService {
    UserDto registerUser(UserRegistrationDto userRegistrationDto);
}
