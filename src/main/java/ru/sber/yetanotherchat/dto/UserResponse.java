package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long peerId;
    private String name;
    private Status status;
}
