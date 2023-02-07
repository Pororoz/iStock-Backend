package com.pororoz.istock.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
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
    LoginResponse loginResponse = LoginResponse.builder()
        .username((String) authentication.getPrincipal())
        .roleName(authentication.getAuthorities().iterator().next().toString()).build();
    ResponseEntity responseEntity = ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, ResponseMessage.Login, loginResponse));
    String result = objectMapper.writeValueAsString(responseEntity.getBody());
    response.getWriter().write(result);

  }

}
