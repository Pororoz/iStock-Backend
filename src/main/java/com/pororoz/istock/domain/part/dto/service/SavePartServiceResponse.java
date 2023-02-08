package com.pororoz.istock.domain.part.dto.service;

import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.entity.Part;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavePartServiceResponse {

  private Long partId;
  private String partName;
  private String spec;
  private long price;
  private long stock;

  public static SavePartServiceResponse of(Part part) {
    return SavePartServiceResponse.builder()
        .partId(part.getId())
        .partName(part.getPartName())
        .spec(part.getSpec())
        .price(part.getPrice())
        .stock(part.getStock())
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
