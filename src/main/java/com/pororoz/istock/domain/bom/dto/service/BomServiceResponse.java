package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BomServiceResponse {

  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private Long partId;
  private Long subAssyId;
  private Long productId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static BomServiceResponse of(Bom bom) {
    return BomServiceResponse.builder()
        .bomId(bom.getId())
        .locationNumber(bom.getLocationNumber())
        .codeNumber(bom.getCodeNumber())
        .quantity(bom.getQuantity())
        .memo(bom.getMemo())
        .subAssyId(bom.getSubAssy() == null ? null : bom.getSubAssy().getId())
        .partId(bom.getPart() == null ? null : bom.getPart().getId())
        .productId(bom.getProduct().getId())
        .createdAt(bom.getCreatedAt())
        .updatedAt(bom.getUpdatedAt())
        .build();
  }

  public BomResponse toResponse() {
    return BomResponse.builder()
        .bomId(bomId)
        .locationNumber(locationNumber)
        .subAssyId(subAssyId)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .partId(partId)
        .productId(productId)
        .build();
  }
}
