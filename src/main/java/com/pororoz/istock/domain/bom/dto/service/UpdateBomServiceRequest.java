package com.pororoz.istock.domain.bom.dto.service;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateBomServiceRequest {

  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private Long partId;
  private Long subAssyId;
  private Long productId;
}
