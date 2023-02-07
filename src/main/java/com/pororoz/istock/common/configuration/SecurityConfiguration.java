package com.pororoz.istock.common.configuration;

import com.pororoz.istock.domain.auth.handler.CustomAuthenticationFailureHandler;
import com.pororoz.istock.domain.auth.handler.CustomAuthenticationManager;
import com.pororoz.istock.domain.auth.handler.CustomAuthenticationSuccessHandler;
import com.pororoz.istock.domain.auth.handler.JsonUsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
    return (web) -> web.ignoring().requestMatchers("/swagger-ui/**", "/api-docs/**", "/profile");
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
        .requestMatchers("/v*/products/**").authenticated()
        .anyRequest().hasRole("ADMIN")
    );

    http.logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/v1/auth/logout"))
        .logoutSuccessUrl("/")
        .deleteCookies();

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
