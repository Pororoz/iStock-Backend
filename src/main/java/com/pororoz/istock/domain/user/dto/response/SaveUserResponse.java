package com.pororoz.istock.domain.user.dto.response;

import com.pororoz.istock.domain.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserResponse {

    private Long id;

    private String username;

    private Role role;

    @Override
    public boolean equals(Object obj){
        SaveUserResponse response = (SaveUserResponse)obj;
        return id.equals(response.getId()) &&
                username.equals(response.getUsername()) &&
                role.equals(response.getRole());
    }
}
