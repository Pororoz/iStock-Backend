package com.pororoz.istock.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {


  @Schema(description = "아이디", example = "username")
  @Size(min = 2)
  @NotBlank
  private String username;

  @Schema(description = "비밀번호", example = "1q2w3e4r")
  @Size(min = 2)
  @NotBlank
  private String password;

}