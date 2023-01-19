package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserRequest {

    @Size(min = 4, max = 20)
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,100}$",
            message = ExceptionMessage.INVALID_PASSWORD)
    private String password;

    @NotEmpty
    private String roleName;

    public SaveUserServiceRequest toService() {
        return SaveUserServiceRequest.builder()
                .username(username)
                .password(password)
                .roleName(roleName)
                .build();
    }
}
