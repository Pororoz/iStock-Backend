package com.pororoz.istock.domain.user.dto.service;

import com.pororoz.istock.domain.user.dto.response.SaveUserResponse;
import com.pororoz.istock.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserServiceResponse {
    private Long id;
    private String username;
    private String roleName;

    @Override
    public boolean equals(Object obj) {
        SaveUserServiceResponse response = (SaveUserServiceResponse) obj;
        return id.equals(response.getId()) &&
                username.equals(response.getUsername()) &&
                roleName.equals(response.getRoleName());
    }

    public static SaveUserServiceResponse of(User user) {
        return SaveUserServiceResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleName(user.getRole().getName())
                .build();
    }

    public SaveUserResponse toResponse() {
        return SaveUserResponse.builder()
                .id(id)
                .username(username)
                .roleName(roleName)
                .build();
    }
}
