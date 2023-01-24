package com.pororoz.istock.domain.user.dto.service;

import com.pororoz.istock.domain.user.dto.response.FindUserResponse;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserServiceResponse {

  private Long id;
  private String username;
  private String roleName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static UserServiceResponse of(User user) {
    return UserServiceResponse.builder().id(user.getId()).username(user.getUsername())
        .roleName(user.getRole().getName()).createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt()).build();
  }

  public UserResponse toResponse() {
    return UserResponse.builder().id(id).username(username).roleName(roleName).build();
  }

  public FindUserResponse toFindResponse() {
    return FindUserResponse.builder().id(id).username(username).roleName(roleName)
        .createdAt(createdAt.toString()).updatedAt(updatedAt.toString()).build();
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
    return Objects.equals(id, that.id) && Objects.equals(username, that.username)
        && Objects.equals(roleName, that.roleName) && Objects.equals(createdAt,
        that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, roleName, createdAt, updatedAt);
  }
}
