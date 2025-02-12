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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_chat_id_gen")
    @SequenceGenerator(name = "app_user_chat_id_gen", sequenceName = "app_user_chat_id_seq", allocationSize = 1)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    @NotNull
    private Chat chat;
}