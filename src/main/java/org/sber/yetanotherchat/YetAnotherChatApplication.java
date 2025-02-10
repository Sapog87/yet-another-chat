package org.sber.yetanotherchat;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.entity.Chat;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.repository.ChatRepository;
import org.sber.yetanotherchat.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@RequiredArgsConstructor
public class YetAnotherChatApplication {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public static void main(String[] args) {
        SpringApplication.run(YetAnotherChatApplication.class, args);
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setName("user1");
        user1.getRoles().add(User.Role.USER);
        user1.setPasswordHash(passwordEncoder.encode("pass"));
        userRepository.save(user1);
        User user2 = new User();
        user2.setUsername("user2");
        user2.setName("user2");
        user2.setPasswordHash(passwordEncoder.encode("pass"));
        user2.getRoles().add(User.Role.USER);
        userRepository.save(user2);
//        User user3 = new User();
//        user3.setUsername("user3");
//        user3.setName("user3");
//        user3.setPasswordHash(passwordEncoder.encode("pass"));
//        user3.getRoles().add(User.Role.USER);
//        userRepository.save(user3);
//        Chat chat1 = new Chat();
//        chat1.setChatType(Chat.Type.PERSONAL);
//        chat1.getMembers().add(user1);
//        chat1.getMembers().add(user2);
//        chatRepository.save(chat1);
//        user1.getChats().add(chat1);
//        userRepository.save(user1);
//        user2.getChats().add(chat1);
//        userRepository.save(user2);
//
//        Chat chat2 = new Chat();
//        chat2.setChatType(Chat.Type.GROUP);
//        chat2.getMembers().add(user1);
//        chat2.getMembers().add(user2);
//        chat2.getMembers().add(user3);
//        chatRepository.save(chat2);
//        user1.getChats().add(chat2);
//        userRepository.save(user1);
//        user2.getChats().add(chat2);
//        userRepository.save(user2);
//        user2.getChats().add(chat2);
//        userRepository.save(user3);
    }
}
