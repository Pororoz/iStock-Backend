package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserRequest {
    private String username;
    private String password;
    private Role role;

    public SaveUserServiceRequest toService() {
        return SaveUserServiceRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
