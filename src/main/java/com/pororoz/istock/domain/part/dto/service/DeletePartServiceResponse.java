package com.pororoz.istock.domain.part.dto.service;

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

}
