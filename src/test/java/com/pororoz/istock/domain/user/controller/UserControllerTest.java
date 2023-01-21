package com.pororoz.istock.domain.user.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.user.dto.request.SaveUserRequest;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
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
    @DisplayName("계정 삭제하기")
    class deleteUser {
        private Long id;
        private String username;
        private String roleName;

        @BeforeEach
        void setup() {
            id = 1L;
            username = "test";
            roleName = "user";
        }

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase {
            @Test
            @DisplayName("존재하는 유저를 삭제하면 삭제된 유저를 반환한다.")
            void saveUser(){
                // given
                UserServiceResponse userServiceResponse = UserServiceResponse.builder()
                        .id(1L)
                        .username(username)
                        .roleName(roleName)
                        .build();
                UserResponse userResponse = userServiceResponse.toResponse();

                // when
                when(userService.deleteUser(any())).thenReturn(userResponse);
                ResponseEntity<ResultDTO<UserResponse>> response = userController.deleteUser(id);

                // then
                assertEquals(Objects.requireNonNull(response.getBody()).getData(), userResponse);
                assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), ResponseStatus.OK);
                assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), ResponseMessage.DELETE_USER);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {
        }
    }

    @Nested
    @DisplayName("계정 생성하기")
    class SaveUser {

        private String username;
        private String password;
        private String roleName;

        @BeforeEach
        void setup() {
            username = "test";
            password = "1234ab";
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
                UserServiceResponse userServiceResponse = UserServiceResponse.builder()
                        .id(1L)
                        .username(username)
                        .roleName(roleName)
                        .build();
                UserResponse userResponse = userServiceResponse.toResponse();

                // when
                when(userService.saveUser(any())).thenReturn(userResponse);
                ResponseEntity<ResultDTO<UserResponse>> response = userController.saveUser(saveUserRequest);

                // then
                assertEquals(Objects.requireNonNull(response.getBody()).getData(), userResponse);
                assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), ResponseStatus.OK);
                assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), ResponseMessage.SAVE_USER);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {

        }

    }
}