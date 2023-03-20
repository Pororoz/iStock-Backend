package com.pororoz.istock.domain.outbound.dto.service;

import com.pororoz.istock.domain.outbound.dto.response.OutboundUpdateResponse;
import com.pororoz.istock.domain.product.entity.ProductIo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutboundUpdateServiceResponse {
  private Long productIoId;
  private Long productId;
  private Long quantity;

  public static OutboundUpdateServiceResponse of(ProductIo productIo) {
    return OutboundUpdateServiceResponse.builder()
        .productIoId(productIo.getId())
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }

  public OutboundUpdateResponse toResponse() {
    return OutboundUpdateResponse.builder()
        .productIoId(productIoId)
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
