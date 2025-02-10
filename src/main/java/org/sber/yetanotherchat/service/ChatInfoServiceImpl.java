package org.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sber.yetanotherchat.dto.chat.ChatInfo;
import org.sber.yetanotherchat.entity.Peer;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.exception.UserNotFoundException;
import org.sber.yetanotherchat.repository.PeerRepository;
import org.sber.yetanotherchat.repository.UserChatRepository;
import org.sber.yetanotherchat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatInfoServiceImpl implements ChatInfoService {
    private final UserChatRepository userChatRepository;
    private final UserRepository userRepository;
    private final PeerRepository peerRepository;

    @Override
    @Transactional
    public List<ChatInfo> findChatsByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return userChatRepository.findAllByUserWithPeers(user).stream()
                .map(uc -> getUserChatDto(uc.getPeer())).toList();
    }

    @Override
    @Transactional
    public List<ChatInfo> findChatsByName(String name) {
        return peerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::getUserChatDto).toList();
    }

    private ChatInfo getUserChatDto(Peer peer) {
        ChatInfo ucDto = new ChatInfo();
        ucDto.setPeerId(peer.getId());
        ucDto.setChatName(peer.getName());
        return ucDto;
    }
}