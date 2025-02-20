package ru.sber.yetanotherchat.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserChatId {
    private Long userId;
    private Long chatId;
}
