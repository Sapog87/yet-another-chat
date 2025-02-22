package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDto {
    private Long id;
    private Long peerId;
    private String text;
    private String senderName;
    private LocalDateTime createdAt;
    private Boolean outgoing;
}