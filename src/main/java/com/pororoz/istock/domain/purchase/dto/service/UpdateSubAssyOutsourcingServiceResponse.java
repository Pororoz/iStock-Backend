package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.purchase.dto.response.UpdateSubAssyOutsourcingResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSubAssyOutsourcingServiceResponse {

  Long productIoId;
  Long productId;
  long quantity;

  public static UpdateSubAssyOutsourcingServiceResponse of(ProductIo productIo) {
    return UpdateSubAssyOutsourcingServiceResponse.builder()
        .productIoId(productIo.getId())
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }

  public UpdateSubAssyOutsourcingResponse toResponse() {
    return UpdateSubAssyOutsourcingResponse.builder()
        .productIoId(productIoId)
        .productId(productId)
        .quantity(quantity)
        .build();
  }
}
