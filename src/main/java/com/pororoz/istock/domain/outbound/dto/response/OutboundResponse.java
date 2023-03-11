package com.pororoz.istock.domain.outbound.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OutboundResponse {

  private Long productId;
  private Long quantity;
}
