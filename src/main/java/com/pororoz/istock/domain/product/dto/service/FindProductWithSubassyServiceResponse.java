package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.product.dto.response.FindProductWithSubassyResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.SubAssyNotFoundByProductNameException;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductWithSubassyServiceResponse {

  private ProductServiceResponse productServiceResponse;
  private List<SubAssyServiceResponse> subAssyServiceResponses;

  public static FindProductWithSubassyServiceResponse of(Product product, List<Product> subAssys) {
    return FindProductWithSubassyServiceResponse.builder()
        .productServiceResponse(ProductServiceResponse.of(product))
        .subAssyServiceResponses(
            // bom의 code number가 11인 것의 product를 SubAssyServiceResponse로 만든다
            product.getBoms().stream()
                .filter(bom -> bom.getCodeNumber().equals("11"))
                .map(bom -> {
                  // bom의 product number와 같은 product를 subAssys에서 찾는다.
                  Product matchingSubAssy = subAssys.stream()
                      .filter(subAssy -> subAssy.getProductNumber().equals(bom.getProductNumber()))
                      .findAny().orElseThrow(SubAssyNotFoundByProductNameException::new);
                  return SubAssyServiceResponse.of(matchingSubAssy, bom.getQuantity());
                }).toList()
        )
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