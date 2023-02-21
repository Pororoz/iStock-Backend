package com.pororoz.istock.domain.bom.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindBomServiceRequest {
  private Integer page;
  private Integer size;
  private Long productId;
}
