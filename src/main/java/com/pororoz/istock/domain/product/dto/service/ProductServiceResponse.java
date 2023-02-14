package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.entity.Product;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductServiceResponse {

  private Long productId;
  private String productName;
  private String productNumber;
  private String codeNumber;
  private long stock;
  private String companyName;
  private Long categoryId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ProductServiceResponse of(Product product) {
    return ProductServiceResponse.builder()
        .productId(product.getId()).productName(product.getProductName())
        .productNumber(product.getProductNumber()).codeNumber(product.getCodeNumber())
        .stock(product.getStock()).companyName(product.getCompanyName())
        .createdAt(product.getCreatedAt()).updatedAt(product.getUpdatedAt())
        .categoryId(product.getCategory().getId())
        .build();
  }

  public ProductResponse toResponse() {
    return ProductResponse.builder()
        .productId(productId).productName(productName)
        .productNumber(productNumber).codeNumber(codeNumber)
        .stock(stock).companyName(companyName)
        .categoryId(categoryId)
        .build();
  }
}
