package com.pororoz.istock.domain.outbound.dto.service;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OutboundUpdateServiceRequest {
  private Long productIoId;
}
