package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ServerError {
    private String error;
    private String message;
    private Integer code;
    private LocalDateTime timestamp;
}
