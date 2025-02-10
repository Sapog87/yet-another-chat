package org.sber.yetanotherchat.service;

import org.sber.yetanotherchat.dto.user.UserCreateDto;
import org.sber.yetanotherchat.dto.user.UserDto;

public interface UserInfoService {
    UserDto getUserById(Long id);

    UserDto createUser(UserCreateDto userCreateDto);
}
