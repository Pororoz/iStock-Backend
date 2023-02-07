package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveProductServiceRequest {

  private String productName;
  private String productNumber;
  private String codeNumber;
  private long stock;
  private String companyName;
  private Long categoryId;

  public Product toProduct(Category category) {
    return Product.builder().productName(productName).productNumber(productNumber)
        .codeNumber(codeNumber)
        .companyName(companyName).stock(stock).category(category).build();
  }
}
