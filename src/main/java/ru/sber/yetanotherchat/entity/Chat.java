package ru.sber.yetanotherchat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_id_gen")
    @SequenceGenerator(name = "chat_id_gen", sequenceName = "chat_id_seq", allocationSize = 1)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "group_chat_name", length = Integer.MAX_VALUE)
    private String groupChatName;

    @Column(name = "is_group", nullable = false)
    @NotNull
    private Boolean isGroup;

    @ManyToMany(mappedBy = "chats")
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private Set<Message> messages = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}