package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Nested
    class SaveUser{

        private Long id;
        private String username;
        private String password;
        private Role role;

        @BeforeEach
        void setup() {
            id = 1L;
            username = "test";
            password = "1234";
            role = Role.builder().name("ADMIN").build();
        }

        @Nested
        class SuccessCase{
            @Test
            @DisplayName("유저를 생성한다.")
            void saveUser(){
                // given
                SaveUserServiceRequest saveUserServiceRequest = SaveUserServiceRequest.builder()
                        .username(username)
                        .password(password)
                        .role(role)
                        .build();

                User resultUser = User.builder()
                        .id(id)
                        .username(username)
                        .password(password)
                        .role(role)
                        .build();

                SaveUserServiceResponse saveUserServiceResponse = SaveUserServiceResponse.builder()
                        .id(id)
                        .role(role)
                        .username(username)
                        .build();

                // when
                when(userRepository.save(any())).thenReturn(resultUser);
                SaveUserServiceResponse result = userService.saveUser(saveUserServiceRequest);

                // then
                assertEquals(result, saveUserServiceResponse);
            }
        }

        @Nested
        class FailCase{

        }
    }
}