package com.pororoz.istock.domain.part.dto.service;

import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.entity.Part;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartServiceResponse {

  private Long partId;
  private String partName;
  private String spec;
  private long price;
  private long stock;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;


  public static PartServiceResponse of(Part part) {
    return PartServiceResponse.builder()
        .partId(part.getId())
        .partName(part.getPartName())
        .spec(part.getSpec())
        .price(part.getPrice())
        .stock(part.getStock())
        .createdAt(part.getCreatedAt())
        .updatedAt(part.getUpdatedAt())
        .build();
  }

  public PartResponse toResponse() {
    return PartResponse.builder()
        .partId(partId)
        .partName(partName)
        .spec(spec)
        .price(price)
        .stock(stock)
        .build();
  }
}
