package org.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.entity.Peer;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.repository.PeerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeerService {
    private final PeerRepository peerRepository;

    public Peer createPeer(User user) {
        Peer peer = new Peer();
        peer.setId(user.getId());
        peer.setName(user.getName());
        return peerRepository.save(peer);
    }
}