package com.example.database.authentication.service;

import com.example.database.authentication.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.database.authentication.entity.User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())) // create the list here and pass, we can use one to many relationship
                        //to generate this values so that we can store multiple roles and authorities
        );
    }
}

