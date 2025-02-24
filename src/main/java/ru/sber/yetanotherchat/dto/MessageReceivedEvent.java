package ru.sber.yetanotherchat.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageReceivedEvent {
    private MessageDto message;
    private List<String> recipients;
}
