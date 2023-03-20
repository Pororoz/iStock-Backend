package com.pororoz.istock.domain.part.dto.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPartServiceRequest {

  private Long partId;
  private String partName;
  private String spec;

  public FindPartServiceRequest(Long partId, String partName, String spec) {
    this.partId = partId;
    this.partName = partName;
    this.spec = spec;
  }
}
