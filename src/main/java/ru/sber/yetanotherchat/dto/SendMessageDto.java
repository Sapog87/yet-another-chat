package ru.sber.yetanotherchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.sber.yetanotherchat.validation.NotZero;

@Data
public class SendMessageDto {
    @NotNull
    @NotZero
    private Long peerId;

    @NotBlank
    private String text;
}