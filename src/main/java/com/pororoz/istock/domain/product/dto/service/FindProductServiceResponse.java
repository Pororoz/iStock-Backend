package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductServiceResponse {

  private Long productId;
  private String productName;
  private String productNumber;
  private String codeNumber;
  private long stock;
  private String companyName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long categoryId;
  private List<PartServiceResponse> partServiceResponses;

  public static FindProductServiceResponse of(Product product) {
    return FindProductServiceResponse.builder().productId(product.getId())
        .productName(product.getProductName()).productNumber(product.getProductNumber())
        .codeNumber(product.getCodeNumber()).stock(product.getStock())
        .companyName(product.getCompanyName()).createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .categoryId(product.getCategory().getId())
        .partServiceResponses(
            product.getBoms().stream().map(bom -> PartServiceResponse.of(bom.getPart())).toList())
        .build();
  }
}
