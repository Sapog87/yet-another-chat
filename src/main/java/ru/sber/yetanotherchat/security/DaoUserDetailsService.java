package ru.sber.yetanotherchat.security;

import lombok.RequiredArgsConstructor;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@RequiredArgsConstructor
public class DaoUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginWithRoles(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                getRoles(user)
        );
    }

    private List<SimpleGrantedAuthority> getRoles(User user) {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .toList();
    }
}