package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupResponseList {
    private List<GroupResponse> groups;
}
