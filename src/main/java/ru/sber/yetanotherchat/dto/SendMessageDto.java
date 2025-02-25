package ru.sber.yetanotherchat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.sber.yetanotherchat.validation.NotZero;

@Data
public class SendMessageDto {
    @NotNull
    @NotZero
    private Long peerId;

    @NotBlank
    @Size(max = 512)
    private String text;
}
