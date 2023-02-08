package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveBomServiceResponse {

  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private Long partId;
  private Long productId;

  public static SaveBomServiceResponse of(Bom bom) {
    return SaveBomServiceResponse.builder()
        .bomId(bom.getId())
        .locationNumber(bom.getLocationNumber())
        .codeNumber(bom.getCodeNumber())
        .quantity(bom.getQuantity())
        .memo(bom.getMemo())
        .partId(bom.getPartId())
        .productId(bom.getProductId())
        .build();
  }
}
