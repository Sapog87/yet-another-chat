package ru.sber.yetanotherchat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "app_user_message")
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_message_id_gen")
    @SequenceGenerator(name = "user_message_id_gen", sequenceName = "user_message_id_seq", allocationSize = 1)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    @NotNull
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    @NotNull
    private Message message;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    @NotNull
    private Chat chat;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}