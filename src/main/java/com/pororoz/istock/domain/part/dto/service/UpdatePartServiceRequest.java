package com.pororoz.istock.domain.part.dto.service;

import com.pororoz.istock.domain.part.entity.Part;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePartServiceRequest {

  private Long partId;
  private String partName;
  private String spec;
  private long price;
  private long stock;

  public Part toPart(){
    return Part.builder()
        .id(partId)
        .partName(partName)
        .spec(spec)
        .price(price)
        .stock(stock)
        .build();
  }
}
