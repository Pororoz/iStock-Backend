package com.pororoz.istock.domain.purchase.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseBulkServiceResponse {

  private Long productId;
  private long amount;

  public static PurchaseBulkServiceResponse of(PurchaseBulkServiceRequest request) {
    return PurchaseBulkServiceResponse.builder()
        .productId(request.getProductId())
        .amount(request.getAmount())
        .build();
  }
}
