package org.sber.yetanotherchat.repository;

import org.sber.yetanotherchat.entity.Peer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeerRepository extends JpaRepository<Peer, Long> {
    List<Peer> findByNameContainingIgnoreCase(String name);
}
