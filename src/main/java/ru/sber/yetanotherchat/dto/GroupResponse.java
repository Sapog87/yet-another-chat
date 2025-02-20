package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {
    private Long peerId;
    private String name;
    private Boolean isMember;
}
