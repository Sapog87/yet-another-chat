package ru.sber.yetanotherchat.repository;


import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query(value = "from User u left join fetch u.roles where u.username = :username")
    Optional<User> findByLoginWithRoles(@Param("username") String username);

    List<User> findAllByChats(Chat chat);

    List<User> findAllByNameContainingIgnoreCase(String name);
}