package com.pororoz.istock.domain.category.dto.service;

import com.pororoz.istock.domain.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveCategoryServiceRequest {

  private String categoryName;

  public Category toCategory() {
    return Category.builder()
        .categoryName(categoryName)
        .build();
  }
}
