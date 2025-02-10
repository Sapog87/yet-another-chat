package org.sber.yetanotherchat.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatInputMessage {
    @NotNull
    private Long peerId;
    @NotBlank
    private String text;
    @NotNull
    private Long randomId;
}