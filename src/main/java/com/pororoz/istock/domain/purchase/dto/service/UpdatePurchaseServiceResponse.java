package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.purchase.dto.response.UpdatePurchaseResponse;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UpdatePurchaseServiceResponse {

  Long partIoId;
  Long partId;
  long quantity;

  public static UpdatePurchaseServiceResponse of(PartIo partIo) {
    return UpdatePurchaseServiceResponse.builder()
        .partIoId(partIo.getId())
        .partId(partIo.getPart().getId())
        .quantity(partIo.getQuantity())
        .build();
  }

  public UpdatePurchaseResponse toResponse() {
    return UpdatePurchaseResponse.builder()
        .partIoId(partIoId)
        .partId(partId)
        .quantity(quantity)
        .build();
  }
}
