package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
    private Long id;
    private String name;
    private Boolean isGroup;
}
