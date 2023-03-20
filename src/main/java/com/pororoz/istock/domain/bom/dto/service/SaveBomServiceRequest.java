package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SaveBomServiceRequest {

  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private Long partId;
  private Long subAssyId;
  private Long productId;

  public Bom toBom(Product product, Product subAssy, Part part) {
    return Bom.builder()
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .part(part)
        .subAssy(subAssy)
        .product(product)
        .build();
  }

}
