package ru.sber.yetanotherchat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_gen")
    @SequenceGenerator(name = "message_id_gen", sequenceName = "message_id_seq", allocationSize = 1)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @NotNull
    private User sender;

    @Column(name = "text", nullable = false)
    @NotBlank
    @Size(max = 512)
    private String text;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    @NotNull
    private Chat chat;

    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;
}
