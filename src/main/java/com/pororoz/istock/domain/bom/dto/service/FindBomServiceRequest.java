package com.pororoz.istock.domain.bom.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindBomServiceRequest {

  private Long productId;
}
