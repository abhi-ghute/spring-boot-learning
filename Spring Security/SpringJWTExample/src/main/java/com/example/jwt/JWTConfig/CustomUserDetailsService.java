package com.example.jwt.JWTConfig;

import com.example.jwt.entity.UserEntity;
import com.example.jwt.repository.UserRepository;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByEmail(username);

        return new User(
                entity.getEmail(),
                entity.getPassword(),
                entity.getAuthorities().stream()
                        .map(a -> new SimpleGrantedAuthority(a.getName().name()))
                        .collect(Collectors.toList())
        );
    }
}
