package com.pororoz.istock.domain.auth.controlloer;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.domain.auth.dto.request.LoginRequest;
import com.pororoz.istock.domain.auth.swagger.response.LoginResponseSwagger;
import com.pororoz.istock.domain.auth.swagger.response.LogoutResponseSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "User API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

  @Operation(summary = "login", description = "유저 로그인")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.LOGIN, content = {
          @Content(schema = @Schema(implementation = LoginResponseSwagger.class))}),
      @ApiResponse(responseCode = "401", description = ExceptionMessage.UNAUTHORIZED, content = @Content)})
  @PostMapping("/login")
  public void login(@RequestBody LoginRequest request) {
  }

  @Operation(summary = "logout", description = "유저 로그아웃")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.LOGOUT, content = {
          @Content(schema = @Schema(implementation = LogoutResponseSwagger.class))})})
  @PostMapping("/logout")
  public void logout() {
  }

  @GetMapping("/admin") // 권한 확인용 api
  public String admin() {
    return "ROLE_ADMIN";
  }
}
