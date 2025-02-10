package org.sber.yetanotherchat.dto.message;

import lombok.Data;

import java.util.List;

@Data
public class MessageReceivedEvent {
    private MessageInfo message;
    private List<String> recipients;
}