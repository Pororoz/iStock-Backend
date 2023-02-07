package com.pororoz.istock.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import com.pororoz.istock.domain.auth.dto.response.result.ResultLoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {
    HttpSession session = request.getSession();
    // session 최대 유효시간 설정 - 단위는 초(sec)
    session.setMaxInactiveInterval(60 * 30);

    response.setHeader("content-type", "application/json");
    response.setCharacterEncoding("UTF-8");
    LoginResponse data = LoginResponse.builder()
        .username((String) authentication.getPrincipal())
        .roleName(authentication.getAuthorities().toString()).build();
    ResultLoginResponse resultLoginResponse = new ResultLoginResponse("OK", "로그인", data);
    String result = objectMapper.writeValueAsString(resultLoginResponse);
    response.getWriter().write(result);

  }

}
