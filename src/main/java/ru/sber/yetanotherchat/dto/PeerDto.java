package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeerDto {
    private Long peerId;
    private String peerName;
    private Status status;
}
