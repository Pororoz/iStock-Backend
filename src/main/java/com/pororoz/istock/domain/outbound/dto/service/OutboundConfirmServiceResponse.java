package com.pororoz.istock.domain.outbound.dto.service;

import com.pororoz.istock.domain.outbound.dto.response.OutboundConfirmResponse;
import com.pororoz.istock.domain.product.entity.ProductIo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutboundConfirmServiceResponse {
  private Long productIoId;
  private Long productId;
  private Long quantity;

  public static OutboundConfirmServiceResponse of(ProductIo productIo) {
    return OutboundConfirmServiceResponse.builder()
        .productIoId(productIo.getId())
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }

  public OutboundConfirmResponse toResponse() {
    return OutboundConfirmResponse.builder()
        .productIoId(productIoId)
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
