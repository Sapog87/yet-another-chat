package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;

import java.util.List;

public interface AccountService {
    UserDto registerUser(UserRegistrationDto userRegistrationDto);

    List<UserDto> getUsersByName(String name);
}