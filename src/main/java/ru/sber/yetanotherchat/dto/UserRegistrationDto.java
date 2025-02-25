package ru.sber.yetanotherchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String username;

    @NotBlank
    @Size(max = 255)
    private CharSequence password;
}
