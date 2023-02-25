package com.pororoz.istock.domain.part.dto;

import com.pororoz.istock.domain.part.entity.Part;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartDto {
  private Long id;
  private String partName;
  private String spec;
  private long price;
  private long stock;

  public static PartDto of(Part part) {
    return PartDto.builder()
        .id(part.getId())
        .partName(part.getPartName())
        .price(part.getPrice())
        .spec(part.getSpec())
        .stock(part.getStock())
        .build();
  }
}
