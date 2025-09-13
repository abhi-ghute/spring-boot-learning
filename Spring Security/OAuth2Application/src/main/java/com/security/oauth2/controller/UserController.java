package com.security.oauth2.controller;

import com.security.oauth2.model.AuthorityEnum;
import com.security.oauth2.model.RoleEnum;
import com.security.oauth2.model.UserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String roleStr = jwt.getClaimAsString("role");
        RoleEnum role = roleStr != null ? RoleEnum.valueOf(roleStr) : RoleEnum.ROLE_USER;
        List<AuthorityEnum> auths = jwt.getClaimAsStringList("authorities").stream()
                .map(AuthorityEnum::valueOf).toList();

        return new UserDto(email, role, "N/A", auths);
    }
}