package com.pororoz.istock.domain.purchase.dto.service;

import static com.pororoz.istock.domain.part.entity.PartStatus.구매대기;

import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseProductServiceRequest {

  private Long productId;
  private long amount;

  public PartIo toPartIo(Part part) {
    return PartIo.builder()
        .quantity(amount)
        .status(구매대기)
        .part(part)
        .build();
  }
}
