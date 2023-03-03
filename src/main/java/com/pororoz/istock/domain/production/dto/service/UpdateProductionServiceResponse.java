package com.pororoz.istock.domain.production.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProductionServiceResponse {

  private Long productIoId;
  private Long productId;
  private Long quantity;

//  public UpdateProductionResponse toResponse() {
//    return UpdateProductionResponse.builder()
//        .productId(productId).quantity(quantity)
//        .build();
//  }
}
