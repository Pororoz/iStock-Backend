package com.pororoz.istock.domain.bom.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BomResponse {
  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private Long partId;
  private Long productId;
}
