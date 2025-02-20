package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.SendMessageDto;

import java.security.Principal;
import java.util.List;

/**
 *
 */
public interface MessagingService {
    /**
     * @param sendMessageDto
     * @param sender
     * @return
     */
    MessageDto sendMessage(SendMessageDto sendMessageDto, Principal sender);

    /**
     * @param peerId
     * @param sender
     * @return
     */
    List<MessageDto> fetchHistory(FetchHistoryDto peerId, Principal sender);
}