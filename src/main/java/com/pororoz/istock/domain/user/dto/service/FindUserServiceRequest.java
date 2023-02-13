package com.pororoz.istock.domain.user.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUserServiceRequest {

  private Integer page;
  private Integer size;
}
