package com.pororoz.istock.domain.auth.dto.request;

import com.pororoz.istock.domain.auth.dto.service.LoginDTO;
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
    @Size(min = 4, max = 20)
    @NotBlank
    private String username;

    @Size(min = 4, max = 20)
    @NotBlank
    private String password;

    public LoginDTO toLoginDto() {
        return LoginDTO.builder()
                .username(this.username)
                .password(this.password)
                .build();
    }
}