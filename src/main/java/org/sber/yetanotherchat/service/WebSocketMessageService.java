package org.sber.yetanotherchat.service;

import org.sber.yetanotherchat.dto.message.ChatInputMessage;
import org.sber.yetanotherchat.dto.message.ChatOutputMessage;
import org.sber.yetanotherchat.dto.message.HistoryInputMessage;
import org.sber.yetanotherchat.dto.message.HistoryOutputMessage;

import java.security.Principal;

public interface WebSocketMessageService {
    ChatOutputMessage sendMessage(ChatInputMessage chatInputMessage, Principal sender);

    HistoryOutputMessage getHistory(HistoryInputMessage peerId, Principal sender);
}