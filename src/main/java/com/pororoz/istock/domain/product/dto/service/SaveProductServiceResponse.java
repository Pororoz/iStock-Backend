package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductServiceResponse {

  private Long id;
  private String productName;
  private String productNumber;
  private String codeNumber;
  private long stock;
  private String companyName;
  private Long categoryId;

  public static SaveProductServiceResponse of(Product product) {
    return SaveProductServiceResponse.builder().id(product.getId()).productName(product.getName())
        .productNumber(product.getProductNumber()).codeNumber(product.getCodeNumber())
        .stock(product.getStock()).companyName(product.getCompanyName())
        .categoryId(product.getCategory().getId()).build();
  }

  public ProductResponse toResponse() {
    return ProductResponse.builder().id(id).productName(productName).productNumber(productNumber)
        .codeNumber(codeNumber).stock(stock).companyName(companyName).categoryId(categoryId)
        .build();
  }
}