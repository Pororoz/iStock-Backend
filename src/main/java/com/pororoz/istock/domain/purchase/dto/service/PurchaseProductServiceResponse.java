package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.purchase.dto.response.PurchaseProductResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseProductServiceResponse {

  private Long productId;
  private long quantity;

  public static PurchaseProductServiceResponse of(PurchaseProductServiceRequest request) {
    return PurchaseProductServiceResponse.builder()
        .productId(request.getProductId())
        .quantity(request.getQuantity())
        .build();
  }

  public PurchaseProductResponse toResponse() {
    return PurchaseProductResponse.builder()
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
