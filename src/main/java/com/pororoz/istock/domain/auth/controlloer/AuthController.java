package com.pororoz.istock.domain.auth.controlloer;

import com.pororoz.istock.domain.auth.dto.request.LoginRequest;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import com.pororoz.istock.domain.auth.dto.response.result.ResultLoginResponse;
import com.pororoz.istock.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "User API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "유저 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
    @PostMapping("/login")
    public ResponseEntity<ResultLoginResponse> login(@Parameter @Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse data = authService.login(loginRequest.toLoginDto());
        ResultLoginResponse response = new ResultLoginResponse("OK", "로그인", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
