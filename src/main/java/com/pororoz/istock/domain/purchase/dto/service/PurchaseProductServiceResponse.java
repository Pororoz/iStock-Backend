package com.pororoz.istock.domain.purchase.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseProductServiceResponse {

  private Long productId;
  private long amount;

  public static PurchaseProductServiceResponse of(PurchaseProductServiceRequest request) {
    return PurchaseProductServiceResponse.builder()
        .productId(request.getProductId())
        .amount(request.getAmount())
        .build();
  }
}
