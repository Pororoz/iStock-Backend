package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.purchase.dto.response.PurchasePartResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchasePartServiceResponse {

  private Long partId;
  private long quantity;

  public static PurchasePartServiceResponse of(PurchasePartServiceRequest request) {
    return PurchasePartServiceResponse.builder()
        .partId(request.getPartId())
        .quantity(request.getQuantity())
        .build();
  }

  public PurchasePartResponse toResponse() {
    return PurchasePartResponse.builder()
        .partId(partId)
        .quantity(quantity)
        .build();
  }
}
