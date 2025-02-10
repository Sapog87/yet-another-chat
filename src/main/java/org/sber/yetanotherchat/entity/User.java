package org.sber.yetanotherchat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"chats", "messages"})
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_gen")
    @SequenceGenerator(name = "user_id_gen", sequenceName = "app_user_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    @NotBlank
    private String name;

    @Column(name = "login", nullable = false, unique = true, length = Integer.MAX_VALUE)
    @NotBlank
    private String username;

    @Column(name = "password_hash", nullable = false, length = Integer.MAX_VALUE)
    @NotBlank
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "app_user_roles")
    @ElementCollection(targetClass = Role.class)
    @NotEmpty
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "app_user_chat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private Set<Chat> chats = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<UserMessage> messages = new HashSet<>();

    public enum Role {
        USER,
        ADMIN
    }
}