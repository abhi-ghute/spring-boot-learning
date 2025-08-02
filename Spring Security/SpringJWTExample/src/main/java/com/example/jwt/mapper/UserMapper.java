package com.example.jwt.mapper;

import com.example.jwt.entity.Authority;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserEntity toEntity(UserDto dto) {
        UserEntity user = new UserEntity();
        user.setEmail(dto.email());
        user.setRole(dto.role());
        user.setPassword(passwordEncoder.encode(dto.password()));

        Set<Authority> authorityEntities = dto.authorities().stream()
                .map(authEnum -> {
                    Authority authority = new Authority();
                    authority.setName(authEnum);
                    authority.setUser(user);   // link authority back to user
                    return authority;
                })
                .collect(Collectors.toSet());

        user.setAuthorities(authorityEntities);
        return user;
    }

    public UserDto toDTO(UserEntity entity) {

        return new UserDto(entity.getEmail(),
                entity.getRole(),"*******",entity.getAuthorities().stream()
                .map(Authority::getName).toList());

    }
}
