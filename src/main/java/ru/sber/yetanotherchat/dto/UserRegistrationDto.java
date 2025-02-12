package ru.sber.yetanotherchat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotBlank
    private CharSequence password;
}