package org.sber.yetanotherchat.dto.message;

import lombok.Data;

import java.util.List;

@Data
public class HistoryOutputMessage {
    private List<MessageInfo> messages;
}