package com.pororoz.istock.domain.auth.controlloer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "User API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

  @Operation(summary = "로그인", description = "유저 로그인")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "NO_CONTENT"),
      @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
  })
  @PostMapping("/login")
  public void login() {
  }

  @GetMapping("/admin") // 권한 확인용 api
  public String admin() {
    return "ROLE_ADMIN";
  }
}
