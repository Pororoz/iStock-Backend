package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.product.dto.response.FindProductResponse;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductServiceResponse {

  private ProductServiceResponse productServiceResponse;
  private List<PartServiceResponse> partServiceResponses;

  public static FindProductServiceResponse of(Product product) {
    return FindProductServiceResponse.builder()
        .productServiceResponse(ProductServiceResponse.of(product))
        .partServiceResponses(
            product.getBoms().stream().map(bom -> PartServiceResponse.of(bom.getPart())).toList())
        .build();
  }

  public FindProductResponse toResponse() {
    return FindProductResponse.builder()
        .productId(productServiceResponse.getProductId())
        .productName(productServiceResponse.getProductName())
        .productNumber(productServiceResponse.getProductNumber())
        .stock(productServiceResponse.getStock())
        .companyName(productServiceResponse.getCompanyName())
        .categoryId(productServiceResponse.getCategoryId())
        .createdAt(TimeEntity.formatTime(productServiceResponse.getCreatedAt()))
        .updatedAt(TimeEntity.formatTime(productServiceResponse.getUpdatedAt()))
        .subAssy(partServiceResponses.stream()
            .map(PartServiceResponse::toResponse).toList()).build();
  }
}
