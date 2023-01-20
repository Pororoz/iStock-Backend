package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
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
            void saveUser(){
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

                DeleteUserServiceResponse deleteUserServiceResponse = DeleteUserServiceResponse.builder()
                        .id(id)
                        .roleName(roleName)
                        .username(username)
                        .build();

                // when
                when(userRepository.findById(id)).thenReturn(Optional.of(resultUser));
                when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
                DeleteUserServiceResponse result = userService.deleteUser(deleteUserServiceRequest);

                // then
                assertEquals(result, deleteUserServiceResponse);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {

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

                SaveUserServiceResponse saveUserServiceResponse = SaveUserServiceResponse.builder()
                        .id(id)
                        .roleName(roleName)
                        .username(username)
                        .build();

                // when
                when(userRepository.save(any())).thenReturn(resultUser);
                when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
                SaveUserServiceResponse result = userService.saveUser(saveUserServiceRequest);

                // then
                assertEquals(result, saveUserServiceResponse);
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