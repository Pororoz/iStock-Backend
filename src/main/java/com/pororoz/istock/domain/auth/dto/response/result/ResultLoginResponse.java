package com.pororoz.istock.domain.auth.dto.response.result;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResultLoginResponse extends ResultDTO<LoginResponse> {
    public ResultLoginResponse(String status, String message, LoginResponse data) {
        super(status, message, data);
    }
}
