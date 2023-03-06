package com.pororoz.istock.domain.production.dto.service;

import com.pororoz.istock.domain.production.dto.response.UpdateProductionResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProductionServiceResponse {

  private Long productIoId;
  private Long productId;
  private Long quantity;

  public UpdateProductionResponse toResponse() {
    return UpdateProductionResponse.builder()
        .productIoId(productIoId)
        .productId(productId)
        .quantity(quantity).build();
  }
}
