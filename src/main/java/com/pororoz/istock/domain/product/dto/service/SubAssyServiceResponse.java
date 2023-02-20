package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.product.dto.response.SubAssyResponse;
import com.pororoz.istock.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubAssyServiceResponse {

  private long quantity;
  ProductServiceResponse productServiceResponse;

  public static SubAssyServiceResponse of(Product product, long quantity) {
    return SubAssyServiceResponse.builder()
        .productServiceResponse(ProductServiceResponse.of(product))
        .quantity(quantity)
        .build();
  }

  public SubAssyResponse toResponse() {
    return SubAssyResponse.builder()
        .productId(productServiceResponse.getProductId())
        .productName(productServiceResponse.getProductName())
        .productNumber(productServiceResponse.getProductNumber())
        .stock(productServiceResponse.getStock())
        .quantity(quantity)
        .build();
  }
}
