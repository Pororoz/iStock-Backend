package com.pororoz.istock.domain.user.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.response.SaveUserResponse;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceResponse;
import com.pororoz.istock.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    @Nested
    @DisplayName("계정 생성하기")
    class SaveUser {

        private String username;
        private String password;
        private String roleName;

        @BeforeEach
        void setup() {
            username = "test";
            password = "1234";
            roleName = "user";
        }

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase {
            @Test
            @DisplayName("유저 생성하기를 성공하면 User 값을 반환받는다.")
            void saveUser(){
                // given
                SaveUserRequest saveUserRequest = SaveUserRequest.builder()
                        .username(username)
                        .password(password)
                        .roleName(roleName)
                        .build();
                SaveUserServiceResponse saveUserServiceResponse = SaveUserServiceResponse.builder()
                        .id(1L)
                        .username(username)
                        .roleName(roleName)
                        .build();
                SaveUserResponse saveUserResponse = saveUserServiceResponse.toResponse();

                // when
                when(userService.saveUser(any())).thenReturn(saveUserServiceResponse);
                ResponseEntity<ResultDTO<SaveUserResponse>> response = userController.saveUser(saveUserRequest);

                // then
                assertEquals(Objects.requireNonNull(response.getBody()).getData(), saveUserResponse);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {

        }

    }
}