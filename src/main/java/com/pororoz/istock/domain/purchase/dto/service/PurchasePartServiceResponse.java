package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.purchase.dto.response.PurchasePartResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchasePartServiceResponse {

  private Long partId;
  private long amount;

  public static PurchasePartServiceResponse of(PurchasePartServiceRequest request) {
    return PurchasePartServiceResponse.builder()
        .partId(request.getPartId())
        .amount(request.getAmount())
        .build();
  }

  public PurchasePartResponse toResponse() {
    return PurchasePartResponse.builder()
        .partId(partId)
        .amount(amount)
        .build();
  }
}
