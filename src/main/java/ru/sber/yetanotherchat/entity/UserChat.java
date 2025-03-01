package ru.sber.yetanotherchat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "app_user_chat")
public class UserChat {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private UserChatId id = new UserChatId();

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @MapsId("chatId")
    @ManyToOne
    @JoinColumn(name = "chat_id")
    @NotNull
    private Chat chat;

    @Column(name = "peer_id", nullable = false)
    @NotNull
    private Long peerId;
}
