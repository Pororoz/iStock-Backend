package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.purchase.dto.response.ConfirmPurchasePartResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConfirmPurchasePartServiceResponse {

  Long partIoId;
  Long partId;
  long quantity;

  public static ConfirmPurchasePartServiceResponse of(PartIo partIo) {
    return ConfirmPurchasePartServiceResponse.builder()
        .partIoId(partIo.getId())
        .partId(partIo.getPart().getId())
        .quantity(partIo.getQuantity())
        .build();
  }

  public ConfirmPurchasePartResponse toResponse() {
    return ConfirmPurchasePartResponse.builder()
        .partIoId(partIoId)
        .partId(partId)
        .quantity(quantity)
        .build();
  }
}
