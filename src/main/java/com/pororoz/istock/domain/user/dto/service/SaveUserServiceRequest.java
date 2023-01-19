package com.pororoz.istock.domain.user.dto.service;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserServiceRequest {
    private String username;
    private String password;
    private Role role;

    public User toUser() {
        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
