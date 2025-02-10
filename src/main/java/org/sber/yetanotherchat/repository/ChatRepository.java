package org.sber.yetanotherchat.repository;

import org.sber.yetanotherchat.entity.Chat;
import org.sber.yetanotherchat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value = "select c from Chat c join UserChat uc on uc.chat = c where uc.peer.id = :peerId and uc.user = :user")
    Optional<Chat> findChatByUserAndPeerId(@Param("user") User user, @Param("peerId") Long peerId);
}