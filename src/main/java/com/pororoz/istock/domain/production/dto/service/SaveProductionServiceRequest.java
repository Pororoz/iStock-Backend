package com.pororoz.istock.domain.production.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductionServiceRequest {

  private Long productId;
  private long quantity;
}
