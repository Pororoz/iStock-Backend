package com.pororoz.istock.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserResponse {

  @Schema(description = "사용자 아이디", example = "1")
  private Long id;

  @Schema(description = "사용자 이름", example = "pororoz")
  private String username;

  @Schema(description = "역할", example = "ROLE_USER")
  private String roleName;
}
