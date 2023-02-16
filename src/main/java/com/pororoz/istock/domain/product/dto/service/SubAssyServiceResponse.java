package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.dto.response.SubAssyResponse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubAssyServiceResponse {

  private Long partId;
  private String partName;
  private String spec;
  private long price;
  private long stock;
  private long quantity;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static SubAssyServiceResponse of(Part part, long quantity) {
    return SubAssyServiceResponse.builder()
        .partId(part.getId())
        .partName(part.getPartName())
        .spec(part.getSpec())
        .price(part.getPrice())
        .stock(part.getStock())
        .quantity(quantity)
        .createdAt(part.getCreatedAt())
        .updatedAt(part.getUpdatedAt())
        .build();
  }

  public SubAssyResponse toResponse() {
    return SubAssyResponse.builder()
        .partId(partId)
        .partName(partName)
        .spec(spec)
        .stock(stock)
        .quantity(quantity)
        .build();
  }
}
