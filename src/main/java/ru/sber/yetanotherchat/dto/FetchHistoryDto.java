package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchHistoryDto {
    private Long peerId;
    private Long offsetId;
    private Integer limit;
}