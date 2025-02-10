package org.sber.yetanotherchat.repository;

import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {
    @Query(value = "select uc, p from UserChat uc join fetch Peer p on uc.peer = p where uc.user = :user")
    List<UserChat> findAllByUserWithPeers(@Param("user") User user);
}