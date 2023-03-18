package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.purchase.dto.response.UpdateSubAssyPurchaseResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSubAssyPurchaseServiceResponse {

  Long productIoId;
  Long productId;
  long quantity;

  public static UpdateSubAssyPurchaseServiceResponse of(ProductIo productIo) {
    return UpdateSubAssyPurchaseServiceResponse.builder()
        .productIoId(productIo.getId())
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }

  public UpdateSubAssyPurchaseResponse toResponse() {
    return UpdateSubAssyPurchaseResponse.builder()
        .productIoId(productIoId)
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
