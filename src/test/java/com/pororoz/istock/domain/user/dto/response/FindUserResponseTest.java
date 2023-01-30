package com.pororoz.istock.domain.user.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindUserResponseTest {

  FindUserResponse response = FindUserResponse.builder().id(1L).username("abc")
      .roleName("ROLE_USER").createdAt("2023-01-01 00:00:00").updatedAt("2023-01-01 00:00:01")
      .build();
  FindUserResponse responseShallowCopy = response;
  FindUserResponse responseDeepCopy = FindUserResponse.builder().id(1L).username("abc")
      .roleName("ROLE_USER").createdAt("2023-01-01 00:00:00").updatedAt("2023-01-01 00:00:01")
      .build();
  FindUserResponse otherResponse1 = FindUserResponse.builder().id(1L).username("abc")
      .roleName("ROLE_USER").createdAt("").updatedAt("2023-01-01 00:00:01").build();
  FindUserResponse otherResponse2 = FindUserResponse.builder().id(1L).username("abc")
      .roleName("ROLE_USER").createdAt("2023-01-01 00:00:00").updatedAt("").build();
  FindUserResponse otherResponse3 = FindUserResponse.builder().id(2L).username("abc")
      .roleName("ROLE_USER").createdAt("2023-01-01 00:00:00").updatedAt("").build();
  User user = User.builder().id(1L).username(response.getUsername())
      .role(Role.builder().name(response.getRoleName()).build()).password("12345678").build();

  @Test
  @DisplayName("equals test")
  void testEquals() {
    assertEquals(response, responseShallowCopy);
    assertEquals(response, responseDeepCopy);
    assertNotEquals(response, otherResponse1);
    assertNotEquals(response, otherResponse2);
    assertNotEquals(response, otherResponse3);
    assertNotEquals(response, null);
    assertNotEquals(response, user);
  }

  @Test
  @DisplayName("hashCode test")
  void testHashCode() {
    assertEquals(response.hashCode(), responseShallowCopy.hashCode());
    assertEquals(response.hashCode(), responseDeepCopy.hashCode());
    assertNotEquals(response.hashCode(), otherResponse1.hashCode());
    assertNotEquals(response.hashCode(), user.hashCode());
  }
}