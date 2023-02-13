package com.pororoz.istock.domain.product.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductServiceRequest {

  private Integer page;
  private Integer size;
  private Long categoryId;
}
