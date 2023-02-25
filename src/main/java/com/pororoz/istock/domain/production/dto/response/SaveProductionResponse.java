package com.pororoz.istock.domain.production.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductionResponse {

  private Long productId;
  private long amount;
}
