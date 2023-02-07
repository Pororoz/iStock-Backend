package com.pororoz.istock.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @InjectMocks
  CustomUserDetailsService customUserDetailsService;

  @Mock
  UserRepository userRepository;


  @Nested
  class LoadUserByUsername {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("유저 이름을 찾을 수 있다면, UserDetails를 반환한다.")
      void loadUserByUsername() {

        //given
        String username = "test";
        String password = "test1234";
        Role roles = Role.builder().roleName("ROLE_USER").build(); // 권한 : ROLE_ADMIN, ROLE_USER
        User user = User.builder().id(1L).username(username).password(password).role(roles).build();

        //when
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        //then
        assertThat(userDetails.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_USER");
        assertThat(userDetails.getUsername(), equalTo(username));
        assertThat(userDetails.getPassword(), equalTo(password));

      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("유저 이름을 찾을 수 없다면, UsernameNotFoundException을 반환한다.")
      void usernameNotFoundException() {

        //given
        String username = "anonymous";

        //then
        when(userRepository.findByUsername(username)).thenThrow(UsernameNotFoundException.class);

        //then
        assertThrows(UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername(username));
      }
    }
  }
}