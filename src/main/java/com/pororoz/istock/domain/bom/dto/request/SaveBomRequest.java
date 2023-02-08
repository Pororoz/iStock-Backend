package com.pororoz.istock.domain.bom.dto.request;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveBomRequest {
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private Long partId;
  private Long productId;

  public SaveBomServiceRequest toService() {
    return SaveBomServiceRequest.builder()
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .partId(partId)
        .productId(productId)
        .build();
  }
}
