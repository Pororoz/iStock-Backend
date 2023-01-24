package com.pororoz.istock.domain.auth.service;

import com.pororoz.istock.domain.auth.dto.CustomUserDetailsDTO;
import com.pororoz.istock.domain.auth.dto.response.LoginResponse;
import com.pororoz.istock.domain.auth.dto.service.LoginDTO;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        Role role = Role.builder().name("ADMIN").build();
        User user = User.builder().id(1L).username("test").password("test1234").roles(List.of(role)).build();
        userRepository.save(user);
    }


    @Nested
    class LoginTest {

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase {

            @Test
            @DisplayName("로그인 성공")
            void login() {

                //given
                String username = "test";
                String password = "test1234";
                LoginDTO dto = LoginDTO.builder().username(username).password(password).build();

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword()));
                CustomUserDetailsDTO principal = (CustomUserDetailsDTO) authentication.getPrincipal();

                //when
                when((Publisher<?>) authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword()))).thenReturn(authentication);
                when((Publisher<?>) authentication.getPrincipal()).thenReturn(principal);

                //then
                LoginResponse response = authService.login(dto);
                assertEquals(response.getUsername(), username);
                assertEquals(response.getRolename(), "ADMIN");

            }


        }
    }

}