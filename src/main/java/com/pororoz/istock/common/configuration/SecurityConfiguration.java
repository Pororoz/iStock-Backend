package com.pororoz.istock.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.auth.handler.CustomAuthenticationFailureHandler;
import com.pororoz.istock.domain.auth.handler.CustomAuthenticationManager;
import com.pororoz.istock.domain.auth.handler.CustomAuthenticationSuccessHandler;
import com.pororoz.istock.domain.auth.handler.JsonUsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

  private final CustomAuthenticationManager customAuthenticationManager;
  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
  private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  @Bean
  public WebSecurityCustomizer configure() {
    return (web) -> web.ignoring()
        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/profile", "/wait");
  }

  @Bean
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.formLogin().disable();

    http.addFilterBefore(
        authenticationFilter(),
        UsernamePasswordAuthenticationFilter.class);

    http.authorizeHttpRequests((authorize) -> authorize
        .requestMatchers("/v*/test/**").permitAll()
        .requestMatchers("/v*/auth/login").anonymous()
        .requestMatchers("/v*/auth/logout").permitAll()
        .requestMatchers("/v*/categories/**").authenticated()
        .requestMatchers("/v*/parts/**").authenticated()
        .requestMatchers("/v*/products/**").authenticated()
        .requestMatchers("/v*/bom/**").authenticated()
        .requestMatchers("/v*/production/**").authenticated()
        .requestMatchers("/v*/purchase/**").authenticated()
        .requestMatchers("/v*/outbounds/**").authenticated()
        .requestMatchers("/v*/product-io/**").authenticated()
        .requestMatchers("/v*/part-io/**").authenticated()
        .anyRequest().hasRole("ADMIN")
    );

    http.logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/v1/auth/logout"))
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .logoutSuccessHandler((request, response, authentication) -> {
          response.setStatus(HttpServletResponse.SC_OK);
          response.setHeader("content-type", "application/json");
          response.setCharacterEncoding("UTF-8");
          ResponseEntity<ResultDTO<Object>> responseEntity = ResponseEntity.ok(
              new ResultDTO<>(ResponseStatus.OK, ResponseMessage.LOGOUT, null));
          String result = new ObjectMapper().writeValueAsString(responseEntity.getBody());
          response.getWriter().write(result);
        });

    http.sessionManagement()
        .maximumSessions(1)
        .maxSessionsPreventsLogin(false);

    return http.build();
  }

  @Bean
  public JsonUsernamePasswordAuthenticationFilter authenticationFilter() {
    JsonUsernamePasswordAuthenticationFilter authenticationFilter = new JsonUsernamePasswordAuthenticationFilter(
        customAuthenticationSuccessHandler,
        customAuthenticationFailureHandler, customAuthenticationManager);

    SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
    authenticationFilter.setSecurityContextRepository(contextRepository);
    return authenticationFilter;
  }
}
