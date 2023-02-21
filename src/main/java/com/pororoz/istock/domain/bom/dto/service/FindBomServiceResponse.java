package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import java.time.LocalDateTime;
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
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Part part;

  static public FindBomServiceResponse of(Bom bom) {
    return FindBomServiceResponse.builder()
        .bomId(bom.getId())
        .locationNumber(bom.getLocationNumber())
        .codeNumber(bom.getCodeNumber())
        .quantity(bom.getQuantity())
        .memo(bom.getMemo())
        .createdAt(bom.getCreatedAt())
        .updatedAt(bom.getUpdatedAt())
        .part(bom.getPart())
        .build();
  }
}
