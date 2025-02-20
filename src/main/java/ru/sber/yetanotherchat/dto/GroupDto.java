package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDto {
    private Long id;
    private String name;
    private Boolean isMember;
}
