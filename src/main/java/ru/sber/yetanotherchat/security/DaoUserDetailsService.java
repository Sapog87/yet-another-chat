package ru.sber.yetanotherchat.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.repository.UserRepository;

import java.util.List;

/**
 * Реализация UserDetailsService, которая ищет пользователей базе данных.
 */
@RequiredArgsConstructor
public class DaoUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                getRoles(user)
        );
    }

    private List<SimpleGrantedAuthority> getRoles(User user) {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .toList();
    }
}
