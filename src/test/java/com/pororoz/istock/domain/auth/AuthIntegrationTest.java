package com.pororoz.istock.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pororoz.istock.domain.auth.dto.request.LoginRequest;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("POST /v1/auth/login - 유저 로그인")
    class Login {
        private final String url = "/v1/auth/login";
        private String username;
        private String password;

        @BeforeEach
        void setUp() {
            username = "test";
            password = "1234";
        }

        @Nested
        @DisplayName("로그인 성공")
        class SuccessCase {

            @Test
            @DisplayName("등록된 유저의 아이디와 패스워드와 동일한 로그인 요청이 들어오면 로그인에 성공한다.")
            void loginSuccess() throws Exception {
                // given
                Role role = Role.builder().name("ADMIN").build();
                User user = User.builder().id(1L).username(username).password(password).roles(List.of(role)).build();
                userRepository.save(user);
                String requestUsername = "test";
                String requestPassword = "1234";
                LoginRequest request = LoginRequest.builder().username(requestUsername).password(requestPassword).build();

                // when
                ResultActions actions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                actions.andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("로그인 실패")
        class FailCase {

            @Test
            @DisplayName("아이디가 존재하지 않는다면 로그인에 실패한다.")
            void loginFailByUsername() throws Exception {
                // given
                Role role = Role.builder().name("ADMIN").build();
                User user = User.builder().id(1L).username(username).password(password).roles(List.of(role)).build();
                userRepository.save(user);
                String requestUsername = "unknown";
                String requestPassword = password;
                LoginRequest request = LoginRequest.builder().username(requestUsername).password(requestPassword).build();

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
                Role role = Role.builder().name("ADMIN").build();
                User user = User.builder().id(1L).username(username).password(password).roles(List.of(role)).build();
                userRepository.save(user);
                String requestUsername = username;
                String requestPassword = "1111";
                LoginRequest request = LoginRequest.builder().username(requestUsername).password(requestPassword).build();

                // when
                ResultActions actions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                actions.andExpect(status().isUnauthorized());
            }
        }
    }
}
