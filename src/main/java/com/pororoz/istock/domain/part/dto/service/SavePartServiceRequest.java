package com.pororoz.istock.domain.part.dto.service;


import com.pororoz.istock.domain.part.entity.Part;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SavePartServiceRequest {

  private String partName;
  private String spec;
  private long price;
  private long stock;

  public Part toPart(){
    return Part.builder()
        .partName(partName)
        .spec(spec)
        .price(price)
        .stock(stock)
        .build();
  }
}
