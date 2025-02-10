package org.sber.yetanotherchat.service;

import org.sber.yetanotherchat.dto.chat.ChatInfo;

import java.util.List;

public interface ChatInfoService {
    List<ChatInfo> findChatsByUser(String user);

    List<ChatInfo> findChatsByName(String name);
}