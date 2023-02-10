package com.pororoz.istock.domain.part.dto.service;

import com.pororoz.istock.domain.part.entity.Part;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletePartServiceResponse {

  private Long partId;
  private String partName;
  private String spec;
  private long price;
  private long stock;

  public static DeletePartServiceResponse of(Part part) {
    return DeletePartServiceResponse.builder()
        .partId(part.getId())
        .partName(part.getPartName())
        .spec(part.getSpec())
        .price(part.getPrice())
        .stock(part.getStock())
        .build();
  }
}
