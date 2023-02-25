package com.pororoz.istock.domain.production.dto.service;

import com.pororoz.istock.domain.production.dto.response.SaveProductionResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductionServiceResponse {

  private Long productId;
  private long quantity;

  public SaveProductionResponse toResponse() {
    return SaveProductionResponse.builder()
        .productId(productId).quantity(quantity)
        .build();
  }
}
