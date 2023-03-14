package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.purchase.dto.response.CancelPurchasePartResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CancelPurchasePartServiceResponse {

  Long partIoId;
  Long partId;
  long quantity;

  public static CancelPurchasePartServiceResponse of(PartIo partIo) {
    return CancelPurchasePartServiceResponse.builder()
        .partIoId(partIo.getId())
        .partId(partIo.getPart().getId())
        .quantity(partIo.getQuantity())
        .build();
  }

  public CancelPurchasePartResponse toResponse() {
    return CancelPurchasePartResponse.builder()
        .partIoId(partIoId)
        .partId(partId)
        .quantity(quantity)
        .build();
  }
}
