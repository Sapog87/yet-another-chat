package org.sber.yetanotherchat.dto.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusOutputMessage {
    private Long userId;
    private UserStatus userStatus;
    private LocalDateTime lastUpdate;

    public enum UserStatus {
        ONLINE,
        OFFLINE
    }
}