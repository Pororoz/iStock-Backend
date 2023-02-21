package com.pororoz.istock.domain.part.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPartServiceRequest {

  private Long partId;
  private String partName;
  private String spec;
}
