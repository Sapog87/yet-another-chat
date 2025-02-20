package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Online {
    private Set<Long> ids;
}
