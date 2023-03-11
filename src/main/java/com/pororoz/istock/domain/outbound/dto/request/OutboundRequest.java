package com.pororoz.istock.domain.outbound.dto.request;

import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OutboundRequest {

  private Long quantity;

  public OutboundServiceRequest toService(Long productId) {
    return OutboundServiceRequest.builder()
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
