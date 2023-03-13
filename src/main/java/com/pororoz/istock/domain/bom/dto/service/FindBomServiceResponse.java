package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.dto.response.FindBomResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBomServiceResponse {

  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private String createdAt;
  private String updatedAt;
  private Long productId;
  private PartServiceResponse partService;
  private ProductServiceResponse subAssyService;

  static public FindBomServiceResponse of(Bom bom) {
    return FindBomServiceResponse.builder()
        .bomId(bom.getId())
        .locationNumber(bom.getLocationNumber())
        .codeNumber(bom.getCodeNumber())
        .quantity(bom.getQuantity())
        .memo(bom.getMemo())
        .createdAt(TimeEntity.formatTime(bom.getCreatedAt()))
        .updatedAt(TimeEntity.formatTime(bom.getUpdatedAt()))
        .productId(bom.getProduct().getId())
        .partService(bom.getPart() == null ? null : PartServiceResponse.of(bom.getPart()))
        .subAssyService(
            bom.getProduct() == null ? null : ProductServiceResponse.of(bom.getProduct()))
        .build();
  }

  public FindBomResponse toResponse() {
    return FindBomResponse.builder()
        .bomId(bomId)
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .productId(productId)
        .part(partService.toResponse())
        .subAssy(subAssyService.toResponse())
        .build();
  }
}
