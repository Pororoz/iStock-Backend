package com.pororoz.istock.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends
    AbstractAuthenticationProcessingFilter {

  public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
  public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
  public static final String HTTP_METHOD = "POST";
  private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
      = new AntPathRequestMatcher("/v1/auth/login", HTTP_METHOD);

  public JsonUsernamePasswordAuthenticationFilter(
      CustomAuthenticationSuccessHandler authenticationSuccessHandler,
      CustomAuthenticationFailureHandler authenticationFailureHandler,
      AuthenticationManager authenticationManager) {
    super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    setAuthenticationSuccessHandler(authenticationSuccessHandler);
    setAuthenticationFailureHandler(authenticationFailureHandler);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException, IOException {
    log.info("Run JsonUsernamePasswordAuthenticationFilter");
    if (!request.getMethod().equals(HTTP_METHOD) || !request.getContentType()
        .equals("application/json")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }

    String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

    Map<String, String> usernamePasswordMap = new ObjectMapper().readValue(messageBody, Map.class);

    String username = usernamePasswordMap.get(SPRING_SECURITY_FORM_USERNAME_KEY);
    String password = usernamePasswordMap.get(SPRING_SECURITY_FORM_PASSWORD_KEY);
    if (username == null || password == null) {
      throw new AuthenticationServiceException("DATA is miss");
    }

    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        username, password);
    return this.getAuthenticationManager().authenticate(authRequest);
  }
}
