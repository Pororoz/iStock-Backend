package com.pororoz.istock.domain.user.dto.service;

import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.entity.User;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserServiceResponse {

  private Long id;
  private String username;
  private String roleName;

  public static UserServiceResponse of(User user) {
    return UserServiceResponse.builder().id(user.getId()).username(user.getUsername())
        .roleName(user.getRole().getName()).build();
  }

  public UserResponse toResponse() {
    return UserResponse.builder().id(id).username(username).roleName(roleName).build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserServiceResponse that = (UserServiceResponse) o;
    return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(
        roleName, that.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, roleName);
  }
}
