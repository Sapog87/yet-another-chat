package org.sber.yetanotherchat.dto.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageInfo {
    private Long id;
    private Long peerId;
    private String text;
    private String senderName;
    private LocalDateTime createdAt;
}