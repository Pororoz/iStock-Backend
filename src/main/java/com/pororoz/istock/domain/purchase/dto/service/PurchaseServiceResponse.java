package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.part.entity.PartIo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseServiceResponse {

  private Long productId;
  private long amount;
}
