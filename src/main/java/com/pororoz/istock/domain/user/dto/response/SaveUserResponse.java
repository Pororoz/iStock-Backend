package com.pororoz.istock.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserResponse {

    @Schema(description = "사용자 아이디", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "pororoz")
    private String username;

    @Schema(description = "역할", example = "user")
    private String roleName;

    @Override
    public boolean equals(Object obj){
        SaveUserResponse response = (SaveUserResponse)obj;
        return id.equals(response.getId()) &&
                username.equals(response.getUsername()) &&
                roleName.equals(response.getRoleName());
    }
}
