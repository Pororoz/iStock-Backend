package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserRequest {
    private Long id;
    private String password;
    private String roleName;

    public UpdateUserServiceRequest toService() {
        return UpdateUserServiceRequest.builder()
                .id(id)
                .password(password)
                .roleName(roleName)
                .build();
    }
}
