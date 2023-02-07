package com.pororoz.istock.domain.auth.dto.response;

import com.pororoz.istock.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {

  @Schema(description = "username", example = "닉네임")
  private String username;

  @Schema(description = "role", example = "ROLE_USER")
  private String roleName;

}
