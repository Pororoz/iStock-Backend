package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductServiceResponse {

  ProductServiceResponse productServiceResponse;
  private List<PartServiceResponse> partServiceResponses;

  public static FindProductServiceResponse of(Product product) {
    return FindProductServiceResponse.builder()
        .productServiceResponse(ProductServiceResponse.of(product))
        .partServiceResponses(
            product.getBoms().stream().map(bom -> PartServiceResponse.of(bom.getPart())).toList())
        .build();
  }
}
