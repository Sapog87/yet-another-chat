package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.SendMessageDto;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.FetchHistoryDto;

import java.security.Principal;
import java.util.List;

public interface MessagingService {
    MessageDto sendMessage(SendMessageDto sendMessageDto, Principal sender);

    List<MessageDto> fetchHistory(FetchHistoryDto peerId, Principal sender);
}