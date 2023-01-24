package com.pororoz.istock.domain.auth.dto.response.result;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResultLoginResponse extends ResultDTO<LoginResponse> {
    public ResultLoginResponse(@NotBlank String status, @NotBlank String message, @NotNull LoginResponse data) {
        super(status, message, data);
    }
}
