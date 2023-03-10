package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserRequest {

  @Schema(description = "사용자 이름", example = "pororoz")
  @Size(min = 2, max = 20,
      message = ExceptionMessage.INVALID_ID)
  private String username;

  @Schema(description = "비밀번호", example = "1q2w3e4r!")
  @Size(min = 2, max = 100,
      message = ExceptionMessage.INVALID_PASSWORD)
  private String password;

  @Schema(description = "권한", example = "ROLE_USER")
  @NotEmpty(message = ExceptionMessage.INVALID_ROLENAME)
  private String roleName;

  public SaveUserServiceRequest toService() {
    return SaveUserServiceRequest.builder()
        .username(username)
        .password(password)
        .roleName(roleName)
        .build();
  }
}
