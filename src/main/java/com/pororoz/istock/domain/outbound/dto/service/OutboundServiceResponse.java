package com.pororoz.istock.domain.outbound.dto.service;

import com.pororoz.istock.domain.product.entity.ProductIo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutboundServiceResponse {

  private Long productId;
  private Long quantity;

  public static OutboundServiceResponse of(ProductIo productIo) {
    return OutboundServiceResponse.builder()
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }
}
