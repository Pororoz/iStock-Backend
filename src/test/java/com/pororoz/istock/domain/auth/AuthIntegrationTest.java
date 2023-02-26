package com.pororoz.istock.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.auth.dto.CustomUserDetailsDTO;
import com.pororoz.istock.domain.auth.dto.request.LoginRequest;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.ResultActions;

public class AuthIntegrationTest extends IntegrationTest {

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  StringRedisTemplate redisTemplate;

  @Nested
  @DisplayName("GET /v1/auth/admin - 유저 role 확인")
  class Authentication {

    private final String url = "/v1/auth/admin";

    private String role;


    @Nested
    @DisplayName("권한 인증 성공")
    class SuccessCase {

      @Test
      @DisplayName("ADMIN 권한을 가진 계정으로 요청 시, 200 코드를 반환한다.")
      public void autheticationSuccess() throws Exception {
        //given
        role = "ADMIN";
        //when
        ResultActions actions = mockMvc.perform(get(url).with(user("ROLE_USER").roles(role)));
        //then
        actions.andExpect(status().isOk());
      }
    }

    @Nested
    @DisplayName("권한 인증 실패")
    class FailCase {

      @Test
      @DisplayName("USER 권한을 가진 계정으로 요청 시, 403 코드를 반환한다.")
      public void authenticationFail() throws Exception {
        //given
        role = "USER";
        //when
        ResultActions actions = mockMvc.perform(get(url).with(user("anonymous").roles(role)));
        //then
        actions.andExpect(status().isForbidden());
      }
    }

  }

  @Nested
  @DisplayName("POST /v1/auth/login - 유저 로그인")
  class Login {

    private final String url = "/v1/auth/login";
    private String username;
    private String password;

    @BeforeEach
    void setUp() {
      username = "ROLE_ADMIN";
      password = passwordEncoder.encode("ROLE_ADMIN");
    }


    @Nested
    @DisplayName("로그인 성공")
    class SuccessCase {

      @Test
      @DisplayName("등록된 유저의 로그인 요청이 들어오면 200 코드 유저 정보를 반환한다.")
      void loginSuccess() throws Exception {
        // given
        Role role = roleRepository.findById(1L).orElseThrow(RoleNotFoundException::new);
        User user = User.builder().id(1L).username(username).password(password).role(role).build();
        userRepository.save(user);
        String requestPassword = "ROLE_ADMIN";
        LoginRequest request = LoginRequest.builder().username(username).password(requestPassword)
            .build();

        // when
        ResultActions actions = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isOk());
      }
    }

    @Nested
    @DisplayName("로그인 실패")
    class FailCase {

      @Test
      @DisplayName("아이디가 존재하지 않는다면 로그인에 실패한다.")
      void loginFailByUsername() throws Exception {
        // given
        Role role = roleRepository.findById(1L).orElseThrow(RoleNotFoundException::new);
        User user = User.builder().id(1L).username(username).password(password).role(role).build();
        userRepository.save(user);
        String requestUsername = "anonymous";
        String requestPassword = "1234";
        LoginRequest request = LoginRequest.builder().username(requestUsername)
            .password(requestPassword).build();

        // when
        ResultActions actions = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isUnauthorized());
      }

      @Test
      @DisplayName("패스워드가 틀린다면 로그인에 실패한다.")
      void loginFailByPassword() throws Exception {
        // given
        Role role = roleRepository.findById(1L).orElseThrow(RoleNotFoundException::new);
        User user = User.builder().id(1L).username(username).password(password).role(role).build();
        userRepository.save(user);
        String requestUsername = username;
        String requestPassword = "123456";
        LoginRequest request = LoginRequest.builder().username(requestUsername)
            .password(requestPassword).build();

        // when
        ResultActions actions = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isUnauthorized());
      }
    }
  }

  @Nested
  @DisplayName("POST /v1/auth/logout - 유저 로그아웃")
  class Logout {

    String uri = "/v1/auth/logout";
    String username = "username";
    String password = "12345678";

    @Test
    @DisplayName("세션을 지워서 로그아웃을 한다.")
    void logout() throws Exception {
      //given
      Role role = roleRepository.findById(1L).orElseThrow();
      User user = userRepository.save(
          User.builder()
              .username(username).password(passwordEncoder.encode(password))
              .role(role).build());
      CustomUserDetailsDTO details = new CustomUserDetailsDTO(user);
      MockHttpSession session = new MockHttpSession();
      SecurityContext sc = SecurityContextHolder.getContext();
      sc.setAuthentication(new UsernamePasswordAuthenticationToken(username, password,
          details.getAuthorities()));
      session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

      //when
      ResultActions actions = mockMvc.perform(
              post(uri).session(session))
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.LOGOUT))
          .andDo(print());

      //then
      HttpSession logoutSession = actions.andExpect(status().isOk())
          .andDo(print())
          .andReturn().getRequest().getSession();
      SecurityContext logoutSc = (SecurityContext) logoutSession.getAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
      assertThat(logoutSc.getAuthentication()).isNull();
    }

  }
}
