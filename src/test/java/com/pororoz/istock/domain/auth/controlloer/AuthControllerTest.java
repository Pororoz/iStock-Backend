package com.pororoz.istock.domain.auth.controlloer;

import com.pororoz.istock.domain.auth.dto.request.LoginRequest;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import com.pororoz.istock.domain.auth.dto.response.result.ResultLoginResponse;
import com.pororoz.istock.domain.auth.dto.service.LoginDTO;
import com.pororoz.istock.domain.auth.service.AuthService;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @InjectMocks
    AuthController authController;

    @Mock
    AuthService authService;

    @Nested
    @DisplayName("로그인")
    class Login{

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase{
            @Test
            @DisplayName("로그인에 성공한 경우")
            void loginTest() {
                //given
                String username = "test";
                String password = "test1234";
                LoginRequest loginRequest = LoginRequest.builder()
                        .username(username)
                        .password(password)
                        .build();
                Role roles = Role.builder().name("ADMIN").build();
                User user = User.builder().id(1L).username(username).password(password).roles(List.of(roles)).build();
                LoginResponse data = LoginResponse.of(user);

                //when
                when(authService.login(loginRequest.toLoginDto())).thenReturn(data);

                //then
                ResponseEntity<ResultLoginResponse> response = authController.login(loginRequest);
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                Assertions.assertEquals(response.getBody().getStatus(), "OK");
                Assertions.assertEquals(response.getBody().getMessage(), "로그인");
                Assertions.assertEquals(response.getBody().getData().getUsername(), username);
                Assertions.assertEquals(response.getBody().getData().getRolename(),"ADMIN");
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase{
            @Test
            @DisplayName("로그인에 실패한 경우 unauthorized 에러를 반환")
            void unAuthorized() {
                //given
                String username = "test";
                String password = "test1234";
                LoginRequest loginRequest = LoginRequest.builder()
                        .username(username)
                        .password(password)
                        .build();

                //when
                when(authService.login(loginRequest.toLoginDto())).thenThrow(HttpClientErrorException.Unauthorized.class);

                //then
                Assertions.assertThrows(HttpClientErrorException.Unauthorized.class, () -> {
                    authController.login(loginRequest);
                });
            }
        }
    }

}