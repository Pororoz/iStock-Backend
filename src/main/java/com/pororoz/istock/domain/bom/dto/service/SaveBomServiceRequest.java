package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveBomServiceRequest {

  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private String productNumber;
  private Long partId;
  private Long productId;

  public Bom toBom(Part part, Product product) {
    return Bom.builder()
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .productNumber(productNumber)
        .part(part)
        .product(product)
        .build();
  }

}
