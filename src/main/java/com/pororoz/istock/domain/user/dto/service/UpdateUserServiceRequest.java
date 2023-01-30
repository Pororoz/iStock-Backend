package com.pororoz.istock.domain.user.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserServiceRequest {

  private Long id;
  private String password;
  private String roleName;
}
