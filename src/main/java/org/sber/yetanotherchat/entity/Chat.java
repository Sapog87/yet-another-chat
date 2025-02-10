package org.sber.yetanotherchat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
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

//    @Column(name = "chat_name", nullable = false)
//    @NotBlank
//    private String chatName;

    @Column(name = "chat_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Type chatType;

    @ManyToMany(mappedBy = "chats")
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "chat")
    private Set<Message> messages = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Type {
        PERSONAL,
        GROUP
    }
}