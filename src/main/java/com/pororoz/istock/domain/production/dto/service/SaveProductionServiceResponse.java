package com.pororoz.istock.domain.production.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductionServiceResponse {

  private Long productId;
  private long amount;
}
