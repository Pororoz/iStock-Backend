package com.pororoz.istock.domain.user.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.user.dto.response.FindUserResponse;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserServiceResponse {

  private Long userId;
  private String username;
  private String roleName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static UserServiceResponse of(User user) {
    return UserServiceResponse.builder().userId(user.getId()).username(user.getUsername())
        .roleName(user.getRole().getRoleName()).createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt()).build();
  }

  public UserResponse toResponse() {
    return UserResponse.builder().userId(userId).username(username).roleName(roleName).build();
  }

  public FindUserResponse toFindResponse() {
    return FindUserResponse.builder().userId(userId).username(username).roleName(roleName)
        .createdAt(TimeEntity.formatTime(createdAt))
        .updatedAt(TimeEntity.formatTime(updatedAt)).build();
  }
}
