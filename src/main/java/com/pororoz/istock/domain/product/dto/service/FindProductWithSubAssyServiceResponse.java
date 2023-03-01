package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.product.dto.response.FindProductWithSubassyResponse;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductWithSubAssyServiceResponse {

  private ProductServiceResponse productServiceResponse;
  private List<SubAssyServiceResponse> subAssyServiceResponses;

  public static FindProductWithSubAssyServiceResponse of(Product product) {
    return FindProductWithSubAssyServiceResponse.builder()
        .productServiceResponse(ProductServiceResponse.of(product))
        .subAssyServiceResponses(
            // bom의 code number가 11인 것의 product를 SubAssyServiceResponse로 만든다
            Optional.ofNullable(product.getBoms()).orElse(Collections.emptyList()).stream()
                .filter(bom -> "11".equals(bom.getCodeNumber()))
                .map(bom -> SubAssyServiceResponse.of(bom.getSubAssy(), bom.getQuantity()))
                .toList())
        .build();
  }

  public FindProductWithSubassyResponse toResponse() {
    return FindProductWithSubassyResponse.builder()
        .productId(productServiceResponse.getProductId())
        .productName(productServiceResponse.getProductName())
        .productNumber(productServiceResponse.getProductNumber())
        .codeNumber(productServiceResponse.getCodeNumber())
        .stock(productServiceResponse.getStock())
        .companyName(productServiceResponse.getCompanyName())
        .categoryId(productServiceResponse.getCategoryId())
        .createdAt(TimeEntity.formatTime(productServiceResponse.getCreatedAt()))
        .updatedAt(TimeEntity.formatTime(productServiceResponse.getUpdatedAt()))
        .subAssy(subAssyServiceResponses.stream()
            .map(SubAssyServiceResponse::toResponse).toList()).build();
  }
}
