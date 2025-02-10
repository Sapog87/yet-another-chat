package org.sber.yetanotherchat.dto.user;

import lombok.Data;

@Data
public class UserCreateDto {
    private String name;
    private String username;
    private CharSequence password;
}