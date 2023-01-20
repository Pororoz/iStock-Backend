package com.pororoz.istock.domain.user.dto.service;

import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserServiceResponse {
    private Long id;
    private String username;
    private String roleName;

    @Override
    public boolean equals(Object obj) {
        UserServiceResponse response = (UserServiceResponse) obj;
        return id.equals(response.getId()) &&
                username.equals(response.getUsername()) &&
                roleName.equals(response.getRoleName());
    }

    public static UserServiceResponse of(User user) {
        return UserServiceResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleName(user.getRole().getName())
                .build();
    }

    public UserResponse toResponse() {
        return UserResponse.builder()
                .id(id)
                .username(username)
                .roleName(roleName)
                .build();
    }
}
