package com.pororoz.istock.domain.auth.dto.response;

import static org.junit.jupiter.api.Assertions.*;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginResponseTest {

  @Test
  @DisplayName("of() Test")
  void of() {
    // given
    String username = "user";
    String rolename = "role";
    Role role = Role.builder()
        .name(rolename)
        .build();
    User user = User.builder()
        .username(username)
        .role(role)
        .build();
    LoginResponse standard = LoginResponse.builder()
        .username(username)
        .rolename(rolename)
        .build();

    // when
    LoginResponse result = LoginResponse.of(user);

    // then
    assertEquals(standard.getClass(), result.getClass());
    assertEquals(standard.getUsername(), result.getUsername());
    assertEquals(standard.getRolename(), result.getRolename());
  }
}