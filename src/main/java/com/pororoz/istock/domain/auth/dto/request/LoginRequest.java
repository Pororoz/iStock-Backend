package com.pororoz.istock.domain.auth.dto.request;

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
    @Size(min = 2)
    @NotBlank
    private String username;

    @Size(min = 2)
    @NotBlank
    private String password;

}