package com.pororoz.istock.domain.product.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductByPartServiceRequest {

  private Long partId;
  private String partName;
}
