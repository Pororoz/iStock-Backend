package com.pororoz.istock.domain.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

  private Long id;
  private String productName;
  private String productNumber;
  private String codeNumber;
  private long stock;
  private String companyName;
  private Long categoryId;
}
