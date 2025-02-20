package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.UserDto;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;

import java.util.List;

/**
 *
 */
public interface AccountService {
    /**
     * @param userRegistrationDto
     * @return
     */
    UserDto registerUser(UserRegistrationDto userRegistrationDto);

    /**
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
    List<UserDto> getUsersByName(String name, Integer page, Integer pageSize);

    /**
     * @param id
     * @return
     */
    UserDto getUsersById(Long id);
}