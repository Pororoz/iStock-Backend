package com.pororoz.istock.domain.user.dto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceResponseTest {

  UserServiceResponse response = UserServiceResponse.builder().id(1L).username("abc")
      .roleName("user").build();
  UserServiceResponse responseShallowCopy = response;
  UserServiceResponse responseDeepCopy = UserServiceResponse.builder().id(1L).username("abc")
      .roleName("user").build();
  UserServiceResponse otherResponse = UserServiceResponse.builder().id(1L).username("abcd")
      .roleName("user").build();
  User user = User.builder().id(1L).username(response.getUsername())
      .role(Role.builder().name(response.getRoleName()).build()).password("12345678").build();

  @Test
  @DisplayName("equals test")
  void testEquals() {
    assertEquals(response, responseShallowCopy);
    assertEquals(response, responseDeepCopy);
    assertNotEquals(response, otherResponse);
    assertNotEquals(response, null);
    assertNotEquals(response, user);
  }

  @Test
  @DisplayName("hashCode test")
  void testHashCode() {
    assertEquals(response.hashCode(), responseShallowCopy.hashCode());
    assertEquals(response.hashCode(), responseDeepCopy.hashCode());
    assertNotEquals(response.hashCode(), otherResponse.hashCode());
    assertNotEquals(response.hashCode(), user.hashCode());
  }
}