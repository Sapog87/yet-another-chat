package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoryDto {
    private List<MessageDto> messages;
}