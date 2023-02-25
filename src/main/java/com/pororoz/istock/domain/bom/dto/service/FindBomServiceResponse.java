package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.dto.response.FindBomResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.dto.PartDto;
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
  private PartDto part;

  static public FindBomServiceResponse of(Bom bom) {
    return FindBomServiceResponse.builder()
        .bomId(bom.getId())
        .locationNumber(bom.getLocationNumber())
        .codeNumber(bom.getCodeNumber())
        .quantity(bom.getQuantity())
        .memo(bom.getMemo())
        .createdAt(TimeEntity.formatTime(bom.getUpdatedAt()))
        .updatedAt(TimeEntity.formatTime(bom.getUpdatedAt()))
        .part(PartDto.of(bom.getPart()))
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
        .part(part)
        .build();
  }
}
