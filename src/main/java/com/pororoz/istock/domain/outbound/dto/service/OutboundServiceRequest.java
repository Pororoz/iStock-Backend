package com.pororoz.istock.domain.outbound.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutboundServiceRequest {

  private Long productId;
  private Long quantity;
}
