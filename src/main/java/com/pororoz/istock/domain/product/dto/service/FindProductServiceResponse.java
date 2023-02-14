package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindProductServiceResponse extends ProductServiceResponse {

  private List<PartServiceResponse> partServiceResponses;

  public static FindProductServiceResponse of(Product product) {
    return FindProductServiceResponse.builder().productId(product.getId())
        .productName(product.getProductName()).productNumber(product.getProductNumber())
        .codeNumber(product.getCodeNumber()).stock(product.getStock())
        .companyName(product.getCompanyName()).categoryId(product.getCategory().getId())
        .createdAt(product.getCreatedAt()).updatedAt(product.getUpdatedAt())
        .partServiceResponses(
            product.getBoms().stream().map(bom -> PartServiceResponse.of(bom.getPart())).toList())
        .build();
  }
}
