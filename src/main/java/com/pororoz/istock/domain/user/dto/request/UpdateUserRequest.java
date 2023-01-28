package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserRequest {

    @Schema(description = "사용자 아이디", example = "1")
    @NotNull
    @Positive(message = ExceptionMessage.INVALID_ID)
    private Long id;

    @Schema(description = "비밀번호", example = "1q2w3e4r!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,100}$",
            message = ExceptionMessage.INVALID_PASSWORD)
    private String password;

    @Schema(description = "권한", example = "ROLE_USER")
    @NotEmpty(message = ExceptionMessage.INVALID_ROLENAME)
    private String roleName;

    public UpdateUserServiceRequest toService() {
        return UpdateUserServiceRequest.builder()
                .id(id)
                .password(password)
                .roleName(roleName)
                .build();
    }
}
