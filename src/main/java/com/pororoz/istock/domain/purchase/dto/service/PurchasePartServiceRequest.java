package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchasePartServiceRequest {
  private Long partId;
  private long amount;

  public PartIo toPartIo(Part part) {
    return PartIo.builder()
        .quantity(amount)
        .status(PartStatus.구매대기)
        .part(part)
        .build();
  }
}
