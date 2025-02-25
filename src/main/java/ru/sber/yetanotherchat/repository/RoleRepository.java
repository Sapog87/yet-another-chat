package ru.sber.yetanotherchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.yetanotherchat.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(Role.UserRole role);
}
