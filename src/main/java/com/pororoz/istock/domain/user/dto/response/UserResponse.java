package com.pororoz.istock.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserResponse {

  @Schema(description = "사용자 아이디", example = "1")
  protected Long id;

  @Schema(description = "사용자 이름", example = "pororoz")
  protected String username;

  @Schema(description = "역할", example = "user")
  protected String roleName;

  @Override
  public boolean equals(Object obj) {
    if (getClass() != obj.getClass()) {
      return false;
    }

    UserResponse response = (UserResponse) obj;
    return id.equals(response.getId()) &&
        username.equals(response.getUsername()) &&
        roleName.equals(response.getRoleName());
  }
}
