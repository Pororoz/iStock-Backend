package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Nested
    @DisplayName("계정 수정 API")
    class UpdateUser {
        private Long id;
        private String username;
        private String password;
        private String newPassword;
        private String roleName;
        private Role role;

        @BeforeEach
        void setup() {
            id = 1L;
            username = "test";
            password = "ab1234";
            newPassword = "abc123";
            roleName = "user";
            role = Role.builder().name("user").build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase {
            @Test
            @DisplayName("존재하는 유저를 업데이트한다.")
            void updateUser(){
                // given
                UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
                        .id(id)
                        .roleName(roleName)
                        .password(newPassword)
                        .build();

                User resultUser = User.builder()
                        .id(id)
                        .username(username)
                        .password(newPassword)
                        .role(role)
                        .build();

                UserServiceResponse userServiceResponse = UserServiceResponse.builder()
                        .id(id)
                        .roleName(roleName)
                        .username(username)
                        .build();
                UserResponse response = userServiceResponse.toResponse();

                // when
                when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
                when(userRepository.findById(any())).thenReturn(Optional.of(resultUser));
                UserResponse result = userService.updateUser(updateUserServiceRequest);

                // then
                assertEquals(result, response);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {
            @Test
            @DisplayName("없는 ID로 요청했을 때 UserNotFoundException을 반환한다.")
            void notExistedId() {
                // given
                long invalidId = 10000L;
                UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
                        .id(invalidId)
                        .password(password)
                        .roleName(roleName)
                        .build();

                // when
                when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

                // then
                assertThrows(UserNotFoundException.class, () -> userService.updateUser(updateUserServiceRequest));
            }

            @Test
            @DisplayName("잘못된 role name은 RoleNotFoundExceptin이 발생한다.")
            void notFoundUser(){
                //given
                String invalidRole = "a";
                UpdateUserServiceRequest updateUserServiceRequest = UpdateUserServiceRequest.builder()
                        .id(id)
                        .password(password)
                        .roleName(invalidRole)
                        .build();

                // when

                //then
                assertThrows(RoleNotFoundException.class, () -> userService.updateUser(updateUserServiceRequest));
            }
        }
    }

    @Nested
    @DisplayName("계정 삭제 API")
    class DeleteUser {
        private Long id;
        private String username;
        private String password;
        private String roleName;
        private Role role;

        @BeforeEach
        void setup() {
            id = 1L;
            username = "test";
            password = "1234";
            roleName = "admin";
            role = Role.builder().name("admin").build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase {
            @Test
            @DisplayName("존재하는 유저를 삭제한다.")
            void deleteUser(){
                // given
                DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
                        .id(id)
                        .build();

                User resultUser = User.builder()
                        .id(id)
                        .username(username)
                        .password(password)
                        .role(role)
                        .build();

                UserServiceResponse userServiceResponse = UserServiceResponse.builder()
                        .id(id)
                        .roleName(roleName)
                        .username(username)
                        .build();
                UserResponse response = userServiceResponse.toResponse();

                // when
                when(userRepository.findById(id)).thenReturn(Optional.of(resultUser));
                UserResponse result = userService.deleteUser(deleteUserServiceRequest);

                // then
                assertEquals(result, response);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {
            @Test
            @DisplayName("없는 ID로 요청했을 때 UserNotFoundException을 반환한다.")
            void notExistedId() {
                // given
                long invalidId = 10000L;
                DeleteUserServiceRequest deleteUserServiceRequest = DeleteUserServiceRequest.builder()
                        .id(invalidId)
                        .build();

                // when

                // then
                assertThrows(UserNotFoundException.class, () -> userService.deleteUser(deleteUserServiceRequest));
            }
        }
    }

    @Nested
    @DisplayName("유저 생성 API Test")
    class SaveUser{

        private Long id;
        private String username;
        private String password;
        private String roleName;
        private Role role;

        @BeforeEach
        void setup() {
            id = 1L;
            username = "test";
            password = "1234";
            roleName = "admin";
            role = Role.builder().name("admin").build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase{
            @Test
            @DisplayName("유저를 생성한다.")
            void saveUser(){
                // given
                SaveUserServiceRequest saveUserServiceRequest = SaveUserServiceRequest.builder()
                        .username(username)
                        .password(password)
                        .roleName(roleName)
                        .build();

                User resultUser = User.builder()
                        .id(id)
                        .username(username)
                        .password(password)
                        .role(role)
                        .build();

                UserServiceResponse userServiceResponse = UserServiceResponse.builder()
                        .id(id)
                        .roleName(roleName)
                        .username(username)
                        .build();

                UserResponse response = userServiceResponse.toResponse();

                // when
                when(userRepository.save(any())).thenReturn(resultUser);
                when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
                UserResponse result = userService.saveUser(saveUserServiceRequest);

                // then
                assertEquals(result, response);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase{
            @Test
            @DisplayName("잘못된 role name은 RoleNotFoundExceptin이 발생한다.")
            void notFoundUser(){
                String invalidRole = "a";

                //given
                SaveUserServiceRequest saveUserServiceRequest = SaveUserServiceRequest.builder()
                        .username(username)
                        .password(password)
                        .roleName(invalidRole)
                        .build();

                //then
                assertThrows(RoleNotFoundException.class, () -> userService.saveUser(saveUserServiceRequest));
            }
        }
    }
}