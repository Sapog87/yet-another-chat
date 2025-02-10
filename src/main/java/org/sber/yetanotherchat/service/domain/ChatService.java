package org.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.entity.Chat;
import org.sber.yetanotherchat.entity.Peer;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.entity.UserChat;
import org.sber.yetanotherchat.repository.ChatRepository;
import org.sber.yetanotherchat.repository.UserChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final PeerService peerService;

    @Transactional
    public Chat createPersonalChat(User firstUser, User secondUser) {
        Peer recipientPeer = peerService.createPeer(secondUser);
        Peer senderPeer = peerService.createPeer(firstUser);
        Chat chat = chatRepository.save(getPersonalChat(firstUser, secondUser));
        userChatRepository.save(getUserChat(firstUser, chat, recipientPeer));
        userChatRepository.save(getUserChat(secondUser, chat, senderPeer));
        return chat;
    }

    private Chat getPersonalChat(User firstUser, User secondUser) {
        Chat chat = new Chat();
        chat.setChatType(Chat.Type.PERSONAL);
        chat.setMembers(Set.of(firstUser, secondUser));
        return chat;
    }

    private UserChat getUserChat(User user, Chat chat, Peer peer) {
        UserChat senderChat = new UserChat();
        senderChat.setChat(chat);
        senderChat.setUser(user);
        senderChat.setPeer(peer);
        return senderChat;
    }
}